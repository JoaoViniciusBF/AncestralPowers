package dev.joaq.ancestralpowers.dimensions;

import dev.joaq.ancestralpowers.components.DimensionalArenaCounter;
import dev.joaq.ancestralpowers.components.MyComponents;
import dev.joaq.ancestralpowers.components.PlayerTraits;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.structure.StructureTemplateManager;


import java.util.EnumSet;
import java.util.Optional;

public class DimensionalArenaStructure {

    private static final Identifier STRUCTURE_ID = new Identifier("ancestralpowers", "dimensional_arena");
    private static final int SPAWN_Y = 64;

    public static void generate(ServerWorld world, int x, int z) {
        StructureTemplateManager manager = world.getStructureTemplateManager();
        Optional<StructureTemplate> optional = manager.getTemplate(STRUCTURE_ID);

        if (optional.isEmpty()) {
            System.err.println("[AncestralPowers] Estrutura 'dimensional_arena' não encontrada!");
            return;
        }

        StructureTemplate template = optional.get();
        BlockPos pos = new BlockPos(x, SPAWN_Y, z);
        template.place(world, pos, pos, new StructurePlacementData(), world.getRandom(), 2);
    }

    public static void teleportToArena(ServerPlayerEntity player, ServerPlayerEntity target) {
        MinecraftServer server = player.getServer();
        if (server == null) return;

        ServerWorld arenaWorld = server.getWorld(ModDimensions.DIMENSIONAL_ARENA_KEY);
        if (arenaWorld == null) {
            player.sendMessage(Text.literal("§cDimensão da arena não encontrada!"), false);
            return;
        }

        PlayerTraits traits = MyComponents.TRAITS.get(player);

        // já dentro? retorna
        if (player.getWorld() == arenaWorld) {
            teleportBack(player);
            if (target != null) teleportBack(target);
            return;
        }

        // salva posição original
        traits.setUsagePosition(player.getPos());
        if (target != null) {
            MyComponents.TRAITS.get(target).setUsagePosition(target.getPos());
        }

        // gera arena se não existir
        if (!traits.getArenaGenerated()) {
            DimensionalArenaCounter counter = DimensionalArenaCounter.getServerState(server);
            int next = counter.incrementAndGet();
            traits.setArenaValue(next);

            int offset = next * 500; // separa bem as arenas
            BlockPos checkPos = new BlockPos(offset, SPAWN_Y, offset);
            if (arenaWorld.getBlockState(checkPos).isAir()) {
                generate(arenaWorld, offset, offset);
            }

            traits.setArenaGenerated(true);
            player.sendMessage(Text.literal("Arena ID: " + next), false);
        }
        if(traits.getArenaGenerated()) {
            int next = traits.getArenaValue();
            int offset = next * 500;
            generate(arenaWorld, offset, offset);
        }


        int value = traits.getArenaValue() * 500;
        Vec3d teleportPos = new Vec3d(value, 73, value);
        EnumSet<PositionFlag> flags = EnumSet.noneOf(PositionFlag.class);

         player.teleport(arenaWorld, teleportPos.x+13.5, teleportPos.y, teleportPos.z+22.5, flags, player.getYaw(), player.getPitch());
         if (target != null)
             target.teleport(arenaWorld, teleportPos.x+13.5, teleportPos.y, teleportPos.z+2.5, flags, player.getYaw(), player.getPitch());

        player.sendMessage(Text.literal("§aVocê foi teleportado para sua Dimensional Arena!"), false);
        if (target != null)
            player.sendMessage(Text.literal("§a" + target.getName().getString() + " foi teleportado junto!"), false);
    }

    public static void teleportBack(ServerPlayerEntity player) {
        PlayerTraits traits = MyComponents.TRAITS.get(player);
        Vec3d returnPos = traits.getUsagePosition();
        if (returnPos == null) return;

         ServerWorld overworld = player.getServer().getOverworld();
         player.teleport(overworld, returnPos.x, returnPos.y, returnPos.z, EnumSet.noneOf(PositionFlag.class),
                 player.getYaw(), player.getPitch());
        traits.clearUsagePosition();
        traits.setInArena(false);
        player.sendMessage(Text.literal("§aVocê retornou da arena."), false);
    }
}
