package dev.joaq.ancestralpowers.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class SummonedGolemEntity extends PathAwareEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int despawnTimer = 0;
    private static final int DESPAWN_TIME = 60 * 20;
    private UUID ownerUuid;

    public SummonedGolemEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.setPersistent();
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 50.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0)
                .add(EntityAttributes.GENERIC_ARMOR, 4.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(3, new FollowOwnerGoal(this));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(6, new LookAroundGoal(this));

        this.targetSelector.add(1, new OwnerAttackerGoal(this));
        this.targetSelector.add(2, new OwnerAttackTargetGoal(this));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MobEntity.class, 10, false, false, (entity) -> {
            return entity instanceof PathAwareEntity && !(entity instanceof SummonedGolemEntity) && entity != this;
        }));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<SummonedGolemEntity> state) {
        if (state.isMoving()) {
            state.getController().setAnimation(RawAnimation.begin().thenLoop("animation.summoned_golem.walk"));
        } else {
            state.getController().setAnimation(RawAnimation.begin().thenLoop("animation.summoned_golem.idle"));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void tick() {
        super.tick();
        
        if (!this.getWorld().isClient && this.ownerUuid != null) {
            this.despawnTimer++;
            if (this.despawnTimer >= DESPAWN_TIME) {
                this.despawnParticles();
                this.discard();
            }
        }
    }

    private void despawnParticles() {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(
                net.minecraft.particle.ParticleTypes.POOF,
                this.getX(), this.getY(), this.getZ(),
                20, 0.5, 0.5, 0.5, 0.1
            );
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.getSource() instanceof PlayerEntity player && this.ownerUuid != null && this.ownerUuid.equals(player.getUuid())) {
            return false;
        }
        return super.damage(source, amount);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("DespawnTimer", this.despawnTimer);
        if (this.ownerUuid != null) {
            nbt.putUuid("OwnerUuid", this.ownerUuid);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.despawnTimer = nbt.getInt("DespawnTimer");
        if (nbt.containsUuid("OwnerUuid")) {
            this.ownerUuid = nbt.getUuid("OwnerUuid");
        }
    }

    public void setOwnerUuid(UUID uuid) {
        this.ownerUuid = uuid;
    }

    public UUID getOwnerUuid() {
        return this.ownerUuid;
    }

    public LivingEntity getOwner() {
        if (this.ownerUuid != null && this.getWorld() instanceof ServerWorld serverWorld) {
            return serverWorld.getPlayerByUuid(this.ownerUuid);
        }
        return null;
    }

    public static class FollowOwnerGoal extends Goal {
        private final SummonedGolemEntity golem;

        public FollowOwnerGoal(SummonedGolemEntity golem) {
            this.golem = golem;
            this.setControls(java.util.EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            LivingEntity owner = this.golem.getOwner();
            if (owner == null) return false;
            return this.golem.squaredDistanceTo(owner) > 12.0;
        }

        @Override
        public void tick() {
            LivingEntity owner = this.golem.getOwner();
            if (owner != null) {
                this.golem.getNavigation().startMovingTo(owner, 1.0);
            }
        }
    }

    public static class OwnerAttackerGoal extends Goal {
        private final SummonedGolemEntity golem;

        public OwnerAttackerGoal(SummonedGolemEntity golem) {
            this.golem = golem;
            this.setControls(java.util.EnumSet.of(Control.TARGET));
        }

        @Override
        public boolean canStart() {
            LivingEntity owner = this.golem.getOwner();
            if (owner == null || !(owner instanceof PathAwareEntity)) return false;
            
            PathAwareEntity pathOwner = (PathAwareEntity) owner;
            LivingEntity attacker = pathOwner.getAttacker();
            if (attacker == null) return false;
            if (attacker == this.golem) return false;
            
            return this.golem.canTarget(attacker);
        }

        @Override
        public void start() {
            LivingEntity owner = this.golem.getOwner();
            if (owner instanceof PathAwareEntity) {
                this.golem.setTarget(((PathAwareEntity)owner).getAttacker());
            }
        }
    }

    public static class OwnerAttackTargetGoal extends Goal {
        private final SummonedGolemEntity golem;

        public OwnerAttackTargetGoal(SummonedGolemEntity golem) {
            this.golem = golem;
            this.setControls(java.util.EnumSet.of(Control.TARGET));
        }

        @Override
        public boolean canStart() {
            LivingEntity owner = this.golem.getOwner();
            if (owner == null || !(owner instanceof PathAwareEntity)) return false;
            
            PathAwareEntity pathOwner = (PathAwareEntity) owner;
            LivingEntity target = pathOwner.getTarget();
            if (target == null) return false;
            if (target == this.golem) return false;
            
            return this.golem.canTarget(target);
        }

        @Override
        public void start() {
            LivingEntity owner = this.golem.getOwner();
            if (owner instanceof PathAwareEntity) {
                this.golem.setTarget(((PathAwareEntity)owner).getTarget());
            }
        }
    }
}