package dev.joaq.ancestralpowers.dimensions;

import dev.joaq.ancestralpowers.AncestralPowers;
import dev.joaq.ancestralpowers.components.DimensionalArenaCounter;
import dev.joaq.ancestralpowers.components.MyComponents;
import dev.joaq.ancestralpowers.components.PlayerTraits;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.Optional;

public class DimensionalArenaStructure {

    private static final Identifier STRUCTURE_ID = new Identifier("ancestralpowers", "dimensional_arena");
    private static final int SPAWN_Y = 64;
    private static final int BOX_SIZE = 10;

    public static void generate(ServerWorld world, int centerX, int centerZ) {
        StructureTemplateManager manager = world.getStructureTemplateManager();
        Optional<StructureTemplate> optional = manager.getTemplate(STRUCTURE_ID);

        if (optional.isPresent()) {
            StructureTemplate template = optional.get();
            BlockPos pos = new BlockPos(centerX, SPAWN_Y, centerZ);
            template.place(world, pos, pos, new StructurePlacementData(), world.getRandom(), 2);
            AncestralPowers.LOGGER.info("[AncestralPowers] Estrutura NBT 'dimensional_arena' colocada em ({}, {}, {})", centerX, SPAWN_Y, centerZ);
        } else {
            AncestralPowers.LOGGER.warn("[AncestralPowers] Estrutura NBT 'dimensional_arena' nao encontrada. Gerando arena de barreira.");
        }

        int half = BOX_SIZE / 2;
        BlockState barrier = Blocks.BARRIER.getDefaultState();

        for (int dx = -half; dx <= half; dx++) {
            for (int dz = -half; dz <= half; dz++) {
                world.setBlockState(new BlockPos(centerX + dx, SPAWN_Y - 1, centerZ + dz), barrier);
            }
        }

        for (int dy = 0; dy < 5; dy++) {
            for (int dx = -half; dx <= half; dx++) {
                world.setBlockState(new BlockPos(centerX + dx, SPAWN_Y + dy, centerZ - half), barrier);
                world.setBlockState(new BlockPos(centerX + dx, SPAWN_Y + dy, centerZ + half), barrier);
            }
            for (int dz = -half; dz <= half; dz++) {
                world.setBlockState(new BlockPos(centerX - half, SPAWN_Y + dy, centerZ + dz), barrier);
                world.setBlockState(new BlockPos(centerX + half, SPAWN_Y + dy, centerZ + dz), barrier);
            }
        }

        for (int dx = -half; dx <= half; dx++) {
            for (int dz = -half; dz <= half; dz++) {
                world.setBlockState(new BlockPos(centerX + dx, SPAWN_Y + 5, centerZ + dz), barrier);
            }
        }
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

        if (player.getWorld() == arenaWorld) {
            teleportBack(player);
            if (target != null) teleportBack(target);
            return;
        }

        traits.setUsagePosition(player.getPos());
        if (target != null) {
            MyComponents.TRAITS.get(target).setUsagePosition(target.getPos());
        }

        if (!traits.getArenaGenerated()) {
            DimensionalArenaCounter counter = DimensionalArenaCounter.getServerState(server);
            int next = counter.incrementAndGet();
            traits.setArenaValue(next);

            int offset = next * 500;
            BlockPos checkPos = new BlockPos(offset, SPAWN_Y, offset);
            if (arenaWorld.getBlockState(checkPos).isAir()) {
                generate(arenaWorld, offset, offset);
            }

            traits.setArenaGenerated(true);
            player.sendMessage(Text.literal("Arena ID: " + next), false);
        }

        int value = traits.getArenaValue() * 500;
        Vec3d teleportPos = new Vec3d(value + 13.5, 73, value + 22.5);
        EnumSet<PositionFlag> flags = EnumSet.noneOf(PositionFlag.class);

        player.teleport(arenaWorld, teleportPos.x, teleportPos.y, teleportPos.z, flags, player.getYaw(), player.getPitch());
        if (target != null)
            target.teleport(arenaWorld, teleportPos.x, teleportPos.y, teleportPos.z + 2, flags, player.getYaw(), player.getPitch());

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