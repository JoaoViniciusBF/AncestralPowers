package dev.joaq.ancestralpowers.powers.main;

import dev.joaq.ancestralpowers.components.MyComponents;
import dev.joaq.ancestralpowers.components.PlayerTraits;
import dev.joaq.ancestralpowers.networking.ModPacketsS2C;
import dev.joaq.ancestralpowers.powers.PowerBase;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.SkeletonEntity;
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
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import java.util.EnumSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SummonSkeletonPower extends PowerBase {

    private static final int SKELETON_COUNT = 4;
    private static final double SPAWN_RADIUS = 2.5;
    private static final int RISE_DURATION_TICKS = 10;
    private static final double RISE_START_OFFSET = -1.5;
    private static final int DESPAWN_TICKS = 1200;

    @Override
    protected void executeLogic(ServerPlayerEntity player, boolean activate, float stamina) {
        if (!activate) return;

        ServerWorld world = player.getServerWorld();
        Vec3d playerPos = player.getPos();

        List<Vec3d> spawnPositions = new ArrayList<>();

        for (int i = 0; i < SKELETON_COUNT; i++) {
            double angle = (2 * Math.PI * i) / SKELETON_COUNT;
            double offsetX = MathHelper.cos((float) angle) * SPAWN_RADIUS;
            double offsetZ = MathHelper.sin((float) angle) * SPAWN_RADIUS;

            double spawnX = playerPos.x + offsetX;
            double spawnZ = playerPos.z + offsetZ;
            
            BlockPos surfacePos = findSurfacePosition(world, new BlockPos((int) spawnX, world.getTopY(), (int) spawnZ));
            double spawnY = surfacePos.getY() + 1.0;

            Vec3d spawnPos = new Vec3d(spawnX, spawnY, spawnZ);
            spawnPositions.add(spawnPos);
            
            double riseStartY = spawnY + RISE_START_OFFSET;
            spawnSummonedSkeleton(world, player, spawnPos, riseStartY, i, RISE_DURATION_TICKS);
        }

        ModPacketsS2C.sendSummonSkeletonEffect(player, spawnPositions);

        world.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.PLAYERS, 1.0f, 1.0f);
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

    private void spawnSummonedSkeleton(ServerWorld world, ServerPlayerEntity owner, Vec3d surfacePos, double riseStartY, int index, int riseDuration) {
        SummonedSkeletonEntity skeleton = new SummonedSkeletonEntity(EntityType.SKELETON, world);
        skeleton.setOwnerUuid(owner.getUuid());
        skeleton.setPosition(surfacePos.x, riseStartY, surfacePos.z);
        skeleton.setPersistent();
        skeleton.setCustomNameVisible(false);
        skeleton.setCanPickUpLoot(false);

        skeleton.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(20.0);
        skeleton.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(4.0);
        skeleton.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);

        skeleton.setAiDisabled(true);
        skeleton.setInvisible(true);
        skeleton.setInvulnerable(true);

        world.spawnEntity(skeleton);

        UUID ownerUuid = owner.getUuid();

        for (int tick = 0; tick <= riseDuration; tick++) {
            final int currentTick = tick;
            world.getServer().execute(() -> {
                if (!skeleton.isAlive()) return;

                double progress = (double) currentTick / riseDuration;
                double yOffset = MathHelper.lerp(progress, RISE_START_OFFSET, 0.0);
                double currentY = surfacePos.y + yOffset;
                skeleton.setPosition(surfacePos.x, currentY, surfacePos.z);

                if (currentTick % 2 == 0) {
                    world.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, surfacePos.x, currentY + 0.1, surfacePos.z,
                        5, 0.2, 0.1, 0.2, 0.01);
                }

                if (currentTick == riseDuration) {
                    skeleton.setAiDisabled(false);
                    skeleton.setInvisible(false);
                    skeleton.setInvulnerable(false);
                    skeleton.setTarget(null);
                    skeleton.setCustomNameVisible(false);

                    world.playSound(null, skeleton.getX(), skeleton.getY(), skeleton.getZ(),
                        SoundEvents.BLOCK_GRAVEL_BREAK, SoundCategory.HOSTILE, 0.8f, 1.2f);
                    world.spawnParticles(ParticleTypes.POOF, skeleton.getX(), skeleton.getY(), skeleton.getZ(),
                        10, 0.3, 0.3, 0.3, 0.05);
                }
            });
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public static class SummonedSkeletonEntity extends SkeletonEntity {
        private UUID ownerUuid;
        private LivingEntity cachedOwner;
        private int lifeTicks = 0;
        private boolean goalsInitialized = false;

        public SummonedSkeletonEntity(EntityType<? extends SkeletonEntity> entityType, ServerWorld world) {
            super(entityType, world);
            this.experiencePoints = 0;
        }

        public void setOwnerUuid(UUID uuid) {
            this.ownerUuid = uuid;
            this.cachedOwner = null;
        }

        public UUID getOwnerUuid() {
            return this.ownerUuid;
        }

        public LivingEntity getOwner() {
            if (this.cachedOwner == null && this.ownerUuid != null && this.getWorld() instanceof ServerWorld serverWorld) {
                this.cachedOwner = serverWorld.getPlayerByUuid(this.ownerUuid);
            }
            return this.cachedOwner;
        }

        @Override
        protected void initGoals() {
            if (this.goalsInitialized) return;
            this.goalsInitialized = true;

            this.goalSelector.add(0, new SwimGoal(this));
            this.goalSelector.add(1, new MeleeAttackGoal(this, 1.2, false));
            this.goalSelector.add(2, new FollowOwnerGoal(this));
            this.goalSelector.add(3, new WanderAroundFarGoal(this, 1.0f, 1.0f));
            this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
            this.goalSelector.add(5, new LookAroundGoal(this));

            this.targetSelector.add(1, new OwnerAttackerGoal(this));
            this.targetSelector.add(2, new ActiveTargetGoal<>(this, LivingEntity.class, 10, true, false, 
                entity -> entity instanceof LivingEntity && entity.isAlive() && this.canTarget((LivingEntity) entity)
                    && entity instanceof net.minecraft.entity.mob.HostileEntity));
        }

        @Override
        public boolean canTarget(LivingEntity target) {
            LivingEntity owner = this.getOwner();
            if (target == owner) return false;
            
            if (target instanceof SummonedSkeletonEntity otherSkeleton) {
                UUID otherOwnerUuid = otherSkeleton.getOwnerUuid();
                if (otherOwnerUuid != null && otherOwnerUuid.equals(this.ownerUuid)) {
                    return false;
                }
            }
            
            if (target instanceof PlayerEntity) return false;
            if (target instanceof net.minecraft.entity.passive.PassiveEntity) return false;
            
            return super.canTarget(target);
        }

        @Override
        public void tick() {
            super.tick();
            if (!this.getWorld().isClient && this.lifeTicks < DESPAWN_TICKS) {
                this.lifeTicks++;
                if (this.lifeTicks >= DESPAWN_TICKS) {
                    this.discard();
                }
            }
        }

        @Override
        public void onDeath(DamageSource source) {
            this.experiencePoints = 0;
            super.onDeath(source);
        }

        @Override
        public void setTarget(LivingEntity target) {
            LivingEntity owner = this.getOwner();
            if (target != owner) {
                super.setTarget(target);
            }
        }

        public static class FollowOwnerGoal extends Goal {
            private final SummonedSkeletonEntity skeleton;
            private LivingEntity owner;

            public FollowOwnerGoal(SummonedSkeletonEntity skeleton) {
                this.skeleton = skeleton;
                this.setControls(EnumSet.of(Control.MOVE));
            }

            @Override
            public boolean canStart() {
                this.owner = this.skeleton.getOwner();
                if (this.owner == null) return false;
                return this.skeleton.squaredDistanceTo(this.owner) > 12.0;
            }

            @Override
            public void tick() {
                if (this.owner != null) {
                    this.skeleton.getNavigation().startMovingTo(this.owner, 1.0);
                }
            }
        }

        public static class OwnerAttackerGoal extends Goal {
            private final SummonedSkeletonEntity skeleton;
            private LivingEntity owner;
            private LivingEntity ownerLastAttacker;

            public OwnerAttackerGoal(SummonedSkeletonEntity skeleton) {
                this.skeleton = skeleton;
                this.setControls(EnumSet.of(Control.TARGET));
            }

            @Override
            public boolean canStart() {
                this.owner = this.skeleton.getOwner();
                if (this.owner == null) return false;
                
                if (this.owner instanceof net.minecraft.entity.mob.MobEntity mobOwner) {
                    this.ownerLastAttacker = mobOwner.getTarget();
                } else {
                    return false;
                }
                
                if (this.ownerLastAttacker == null) return false;
                
                if (this.ownerLastAttacker == this.skeleton) return false;
                return this.skeleton.canTarget(this.ownerLastAttacker);
            }

            @Override
            public void start() {
                this.skeleton.setTarget(this.ownerLastAttacker);
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