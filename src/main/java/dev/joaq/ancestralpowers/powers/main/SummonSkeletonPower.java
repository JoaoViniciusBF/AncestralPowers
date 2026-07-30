package dev.joaq.ancestralpowers.powers.main;

import dev.joaq.ancestralpowers.components.MyComponents;
import dev.joaq.ancestralpowers.components.PlayerTraits;
import dev.joaq.ancestralpowers.entity.SummonedGolemEntity;
import dev.joaq.ancestralpowers.networking.ModPacketsS2C;
import dev.joaq.ancestralpowers.powers.PowerBase;
import dev.joaq.ancestralpowers.registry.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SummonSkeletonPower extends PowerBase {

    private static final int GOLEM_COUNT = 2;
    private static final double SPAWN_RADIUS = 2.5;
    private static final int RISE_DURATION_TICKS = 10;
    private static final double RISE_START_OFFSET = -1.5;

    @Override
    protected void executeLogic(ServerPlayerEntity player, boolean activate, float stamina) {
        if (!activate) return;

        ServerWorld world = player.getServerWorld();
        Vec3d playerPos = player.getPos();

        List<Vec3d> spawnPositions = new ArrayList<>();

        for (int i = 0; i < GOLEM_COUNT; i++) {
            double angle = (2 * Math.PI * i) / GOLEM_COUNT;
            double offsetX = MathHelper.cos((float) angle) * SPAWN_RADIUS;
            double offsetZ = MathHelper.sin((float) angle) * SPAWN_RADIUS;

            double spawnX = playerPos.x + offsetX;
            double spawnZ = playerPos.z + offsetZ;
            
            BlockPos surfacePos = findSurfacePosition(world, new BlockPos((int) spawnX, world.getTopY(), (int) spawnZ));
            double spawnY = surfacePos.getY() + 1.0;

            Vec3d spawnPos = new Vec3d(spawnX, spawnY, spawnZ);
            spawnPositions.add(spawnPos);
            
            double riseStartY = spawnY + RISE_START_OFFSET;
            spawnSummonedGolem(world, player, spawnPos, riseStartY, RISE_DURATION_TICKS);
        }

        ModPacketsS2C.sendSummonSkeletonEffect(player, spawnPositions);

        world.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.ENTITY_IRON_GOLEM_DEATH, SoundCategory.PLAYERS, 1.0f, 1.0f);
    }

    private BlockPos findSurfacePosition(ServerWorld world, BlockPos startPos) {
        BlockPos pos = startPos;
        while (pos.getY() > world.getBottomY()) {
            BlockState state = world.getBlockState(pos);
            if (!state.isAir() && state.isOpaqueFullCube(world, pos)) {
                return pos.up();
            }
            pos = pos.down();
        }
        return new BlockPos(pos.getX(), world.getSeaLevel(), pos.getZ());
    }

    private void spawnSummonedGolem(ServerWorld world, ServerPlayerEntity owner, Vec3d surfacePos, double riseStartY, int riseDuration) {
        SummonedGolemEntity golem = new SummonedGolemEntity(ModEntities.SUMMONED_GOLEM, world);
        golem.setOwnerUuid(owner.getUuid());
        golem.setPosition(surfacePos.x, riseStartY, surfacePos.z);
        golem.setPersistent();
        golem.setCustomNameVisible(false);
        golem.setCanPickUpLoot(false);

        golem.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(50.0);
        golem.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(8.0);
        golem.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);
        golem.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.5);
        golem.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).setBaseValue(32.0);

        golem.setAiDisabled(true);
        golem.setInvisible(true);
        golem.setInvulnerable(true);

        world.spawnEntity(golem);

        for (int tick = 0; tick <= riseDuration; tick++) {
            final int currentTick = tick;
            world.getServer().execute(() -> {
                if (!golem.isAlive()) return;

                double progress = (double) currentTick / riseDuration;
                double yOffset = MathHelper.lerp(progress, RISE_START_OFFSET, 0.0);
                double currentY = surfacePos.y + yOffset;
                golem.setPosition(surfacePos.x, currentY, surfacePos.z);

                if (currentTick % 2 == 0) {
                    world.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, surfacePos.x, currentY + 0.1, surfacePos.z,
                        5, 0.2, 0.1, 0.2, 0.01);
                }

                if (currentTick == riseDuration) {
                    golem.setAiDisabled(false);
                    golem.setInvisible(false);
                    golem.setInvulnerable(false);
                    golem.setTarget(null);
                    golem.setCustomNameVisible(false);

                    world.playSound(null, golem.getX(), golem.getY(), golem.getZ(),
                        SoundEvents.BLOCK_GRAVEL_BREAK, SoundCategory.HOSTILE, 0.8f, 1.2f);
                    world.spawnParticles(ParticleTypes.POOF, golem.getX(), golem.getY(), golem.getZ(),
                        10, 0.3, 0.3, 0.3, 0.05);
                }
            });
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @Override
    protected float staminaCost() {
        return 40;
    }

    @Override
    public String ActivationType() {
        return "PRESS";
    }

    @Override
    protected void disablePowerSpecific(ServerPlayerEntity player) {
    }

    @Override
    public void apply(ServerPlayerEntity player, boolean activate, float stamina) {
        PlayerTraits traits = MyComponents.TRAITS.get(player);
        execute(player, activate, ActivationType(), traits, "Main");
    }
}