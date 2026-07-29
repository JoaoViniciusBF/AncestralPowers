package dev.joaq.ancestralpowers.client;

import dev.joaq.ancestralpowers.networking.packet.s2c.SummonSkeletonEffectPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class SummonSkeletonEffectHandler {

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(SummonSkeletonEffectPayload.ID, (client, handler, buf, sender) -> {
            SummonSkeletonEffectPayload payload = SummonSkeletonEffectPayload.read(buf);
            client.execute(() -> {
                ClientWorld world = client.world;
                if (world == null) return;

                for (Vec3d pos : payload.spawnPositions()) {
                    spawnSummonEffect(world, pos);
                }
            });
        });
    }

    private static void spawnSummonEffect(ClientWorld world, Vec3d pos) {
        BlockPos blockPos = BlockPos.ofFloored(pos);

        world.addParticle(ParticleTypes.WITCH, pos.x, pos.y, pos.z, 0, 0.1, 0);
        world.addParticle(ParticleTypes.DRAGON_BREATH, pos.x, pos.y + 0.5, pos.z, 0, 0.05, 0);
        world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, pos.x, pos.y + 0.2, pos.z, 0, 0.1, 0);

        for (int i = 0; i < 8; i++) {
            double angle = (i / 8.0) * 2 * Math.PI;
            double offsetX = Math.cos(angle) * 0.5;
            double offsetZ = Math.sin(angle) * 0.5;
            world.addParticle(ParticleTypes.ENCHANT, pos.x + offsetX, pos.y + 0.1, pos.z + offsetZ, 0, 0, 0);
        }

        world.playSound(pos.x, pos.y, pos.z, SoundEvents.BLOCK_GRAVEL_BREAK, SoundCategory.BLOCKS, 0.8f, 1.2f, false);
        world.playSound(pos.x, pos.y, pos.z, SoundEvents.ENTITY_SKELETON_AMBIENT, SoundCategory.HOSTILE, 0.6f, 1.5f, false);

        world.addBlockBreakParticles(blockPos, world.getBlockState(blockPos));
    }

    public static void scheduleEmergenceParticles(MinecraftClient client, ClientWorld world, Vec3d pos, int delayTicks, int totalTicks) {
        client.execute(() -> {
            new Thread(() -> {
                try {
                    Thread.sleep(delayTicks * 50L);
                } catch (InterruptedException ignored) {}

                client.execute(() -> {
                    for (int i = 0; i < totalTicks; i++) {
                        final int tick = i;
                        client.execute(() -> {
                            if (world == null || world.getPlayers().isEmpty()) return;
                            
                            double progress = (double) tick / totalTicks;
                            double yOffset = -0.5 + progress;
                            
                            world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, pos.x, pos.y + yOffset, pos.z, 0, 0.1, 0);
                            world.addParticle(ParticleTypes.SMOKE, pos.x, pos.y + yOffset, pos.z, 0, 0.1, 0);
                            
                            if (tick % 4 == 0) {
                                world.playSound(pos.x, pos.y, pos.z, SoundEvents.BLOCK_SAND_STEP, SoundCategory.BLOCKS, 0.3f, 1.0f + (float) progress * 0.5f, false);
                            }
                        });
                        
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException ignored) {
                            return;
                        }
                    }
                });
            }).start();
        });
    }
}