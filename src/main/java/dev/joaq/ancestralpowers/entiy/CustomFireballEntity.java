package dev.joaq.ancestralpowers.entiy;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

public class CustomFireballEntity extends FireballEntity {

    private float explosionPower = 2.0f;
    private float damageAmount = 6.0f;
    private boolean breakBlocks = false;
    private boolean createFire = false;

    public CustomFireballEntity(EntityType<? extends CustomFireballEntity> type, World world) {
        super(type, world);
    }

     public CustomFireballEntity(World world, LivingEntity owner, double dirX, double dirY, double dirZ) {
         super(world, owner, dirX, dirY, dirZ, 1);
     }

    public void setExplosionPower(float power) {
        this.explosionPower = power;
    }

    public void setDamageAmount(float damage) {
        this.damageAmount = damage;
    }

    public void setBreakBlocks(boolean breakBlocks) {
        this.breakBlocks = breakBlocks;
    }

    public void setCreateFire(boolean createFire) {
        this.createFire = createFire;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);

        if (!this.getWorld().isClient) {
            Entity owner = this.getOwner();

            this.getWorld().createExplosion(
                    this,
                    this.getX(), this.getY(), this.getZ(),
                    this.explosionPower,
                    this.createFire,
                    this.breakBlocks ? World.ExplosionSourceType.MOB : World.ExplosionSourceType.NONE
            );
            this.discard();
        }
    }
}