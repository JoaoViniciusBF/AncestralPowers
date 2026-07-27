package dev.joaq.ancestralpowers.dimensions;

import dev.joaq.ancestralpowers.components.MyComponents;
import dev.joaq.ancestralpowers.components.PersonalDimensionCounter;
import dev.joaq.ancestralpowers.components.PlayerTraits;
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

public class PersonalDimensionStructure {

    private static final int SPAWN_Y = 64;
    private static final Identifier STRUCTURE_ID = new Identifier("ancestralpowers", "personal_dimension_structure");

    // Geração da estrutura
    public static void generate(ServerWorld world, int i, int k) {
        StructureTemplateManager manager = world.getStructureTemplateManager();
        Optional<StructureTemplate> optional = manager.getTemplate(STRUCTURE_ID);

        if (optional.isEmpty()) {
            System.err.println("[AncestralPowers] Estrutura 'personal_spawn' não encontrada!");
            return;
        }

        StructureTemplate template = optional.get();
        BlockPos pos = new BlockPos(i, SPAWN_Y, k);
        template.place(world, pos, pos, new StructurePlacementData(), world.getRandom(), 2);
    }

    // Teleporta player e optional target para a dimensão pessoal
    public static void teleportToPersonalDimension(ServerPlayerEntity player, ServerPlayerEntity target) {
        MinecraftServer server = player.getServer();
        if (server == null) return;

        PlayerTraits playerTraits = MyComponents.TRAITS.get(player);
        ServerWorld personalWorld = server.getWorld(ModDimensions.PERSONAL_WORLD_KEY);

        if (personalWorld == null) {
            player.sendMessage(Text.literal("§cDimensão pessoal não encontrada!"), false);
            return;
        }

        // Se já está na dimensão pessoal, retorna
        if (player.getWorld() == personalWorld) {
            teleportBack(player, playerTraits);
            if (target != null) {
                PlayerTraits targetTraits = MyComponents.TRAITS.get(target);
                teleportBack(target, targetTraits);
            }
            return;
        }

        // Salva posições de origem
        Vec3d playerOrigin = player.getPos();
        playerTraits.setUsagePosition(playerOrigin);

        if (target != null) {
            PlayerTraits targetTraits = MyComponents.TRAITS.get(target);
            targetTraits.setUsagePosition(target.getPos());
        }

        // Geração da dimensão pessoal
        if (!playerTraits.getPersonalDimensionGenerated()) {
            PersonalDimensionCounter globalCounter = PersonalDimensionCounter.getServerState(server);
            int nextValue = globalCounter.incrementAndGet();
            playerTraits.setPersonalDimensionValue(nextValue);

            int offset = nextValue * 500;
            BlockPos checkPos = new BlockPos(offset, SPAWN_Y, offset);
            if (personalWorld.getBlockState(checkPos).isAir()) {
                generate(personalWorld, offset, offset);
            }

            playerTraits.setPersonalDimensionGenerated(true);
            player.sendMessage(Text.literal("Número da dimensão pessoal: " + playerTraits.getPersonalDimensionValue()));
        }

        int value = playerTraits.getPersonalDimensionValue() * 500;
        Vec3d teleportPos = new Vec3d(23.5 + value, 89, 23.5 + value);
        EnumSet<PositionFlag> flags = EnumSet.noneOf(PositionFlag.class);

        // Teleporta player
         player.teleport(personalWorld, teleportPos.x, teleportPos.y, teleportPos.z, flags, player.getYaw(), player.getPitch());

         if (target != null) {
             target.teleport(personalWorld, teleportPos.x, teleportPos.y, teleportPos.z, flags, player.getYaw(), player.getPitch());
         }

        player.sendMessage(Text.literal("§aVocê foi teleportado para sua dimensão pessoal."), false);
        if (target != null) {
            player.sendMessage(Text.literal("§a" + target.getName().getString() + " também foi teleportado junto."), false);
        }
    }

    // Teleporta de volta para a posição de origem
    private static void teleportBack(ServerPlayerEntity player, PlayerTraits traits) {
        Vec3d returnPos = traits.getUsagePosition();
        if (returnPos == null) {
            ServerWorld overworld = player.getServer().getOverworld();
            returnPos = new Vec3d(overworld.getSpawnPos().getX(), overworld.getSpawnPos().getY(), overworld.getSpawnPos().getZ());
        }

         player.teleport(player.getServer().getOverworld(),
                 returnPos.x,
                 returnPos.y,
                 returnPos.z,
                 EnumSet.noneOf(PositionFlag.class),
                 player.getYaw(),
                 player.getPitch());

        traits.clearUsagePosition();
        player.sendMessage(Text.literal("§aVocê retornou para sua posição original."), false);
    }
}
