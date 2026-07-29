package dev.joaq.ancestralpowers.dimensions;

import dev.joaq.ancestralpowers.components.MyComponents;
import dev.joaq.ancestralpowers.components.PersonalDimensionCounter;
import dev.joaq.ancestralpowers.components.PlayerTraits;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class PersonalDimensionStructure {

    private static final int SPAWN_Y = 64;
    private static final int BOX_RADIUS = 11;
    private static final int BOX_HEIGHT = 23;

    public static void generate(ServerWorld world, int centerX, int centerZ) {
        BlockState barrier = Blocks.BARRIER.getDefaultState();
        BlockState bedrock = Blocks.BEDROCK.getDefaultState();

        int floorY = SPAWN_Y - 1;
        int ceilY = floorY + BOX_HEIGHT - 1;

        for (int dx = -BOX_RADIUS; dx <= BOX_RADIUS; dx++) {
            for (int dz = -BOX_RADIUS; dz <= BOX_RADIUS; dz++) {
                world.setBlockState(new BlockPos(centerX + dx, floorY, centerZ + dz), barrier);
            }
        }

        for (int dy = 1; dy < BOX_HEIGHT - 1; dy++) {
            for (int dx = -BOX_RADIUS; dx <= BOX_RADIUS; dx++) {
                world.setBlockState(new BlockPos(centerX + dx, floorY + dy, centerZ - BOX_RADIUS), barrier);
                world.setBlockState(new BlockPos(centerX + dx, floorY + dy, centerZ + BOX_RADIUS), barrier);
            }
            for (int dz = -BOX_RADIUS; dz <= BOX_RADIUS; dz++) {
                world.setBlockState(new BlockPos(centerX - BOX_RADIUS, floorY + dy, centerZ + dz), barrier);
                world.setBlockState(new BlockPos(centerX + BOX_RADIUS, floorY + dy, centerZ + dz), barrier);
            }
        }

        for (int dx = -BOX_RADIUS; dx <= BOX_RADIUS; dx++) {
            for (int dz = -BOX_RADIUS; dz <= BOX_RADIUS; dz++) {
                world.setBlockState(new BlockPos(centerX + dx, ceilY, centerZ + dz), barrier);
            }
        }

        world.setBlockState(new BlockPos(centerX, floorY + BOX_HEIGHT / 2, centerZ), bedrock);
    }

    public static void teleportToPersonalDimension(ServerPlayerEntity player, ServerPlayerEntity target) {
        MinecraftServer server = player.getServer();
        if (server == null) return;

        PlayerTraits playerTraits = MyComponents.TRAITS.get(player);
        ServerWorld personalWorld = server.getWorld(ModDimensions.PERSONAL_WORLD_KEY);

        if (personalWorld == null) {
            player.sendMessage(Text.literal("§cDimensão pessoal não encontrada!"), false);
            return;
        }

        if (player.getWorld() == personalWorld) {
            teleportBack(player, playerTraits);
            if (target != null) {
                PlayerTraits targetTraits = MyComponents.TRAITS.get(target);
                teleportBack(target, targetTraits);
            }
            return;
        }

        Vec3d playerOrigin = player.getPos();
        playerTraits.setUsagePosition(playerOrigin);

        if (target != null) {
            PlayerTraits targetTraits = MyComponents.TRAITS.get(target);
            targetTraits.setUsagePosition(target.getPos());
        }

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
        Vec3d teleportPos = new Vec3d(0.5 + value, SPAWN_Y + 11, 0.5 + value);
        EnumSet<PositionFlag> flags = EnumSet.noneOf(PositionFlag.class);

        player.teleport(personalWorld, teleportPos.x, teleportPos.y, teleportPos.z, flags, player.getYaw(), player.getPitch());

        if (target != null) {
            target.teleport(personalWorld, teleportPos.x + 2, teleportPos.y, teleportPos.z + 2, flags, player.getYaw(), player.getPitch());
        }

        player.sendMessage(Text.literal("§aVocê foi teleportado para sua dimensão pessoal."), false);
        if (target != null) {
            player.sendMessage(Text.literal("§a" + target.getName().getString() + " também foi teleportado junto."), false);
        }
    }

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