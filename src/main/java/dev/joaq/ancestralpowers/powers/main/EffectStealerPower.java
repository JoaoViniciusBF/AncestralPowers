package dev.joaq.ancestralpowers.powers.main;

import dev.joaq.ancestralpowers.components.MyComponents;
import dev.joaq.ancestralpowers.components.PlayerTraits;
import dev.joaq.ancestralpowers.powers.PowerBase;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class EffectStealerPower extends PowerBase {

    @Override
    public void apply(ServerPlayerEntity player, boolean activate, float stamina) {
        PlayerTraits traits = MyComponents.TRAITS.get(player);
        execute(player, activate, ActivationType(), traits, "Main");
    }

    @Override
    protected float staminaCost() {
        return 25f;
    }

    @Override
    protected String ActivationType() {
        return "PRESS";
    }

    @Override
    protected void disablePowerSpecific(ServerPlayerEntity player) {

    }

    @Override
    protected void executeLogic(ServerPlayerEntity player, boolean activate, float stamina) {
        LivingEntity target = getLivingEntityLookedAt(player, 20.0D);
        
        if (target == null) {
            player.sendMessage(Text.literal("Nenhum alvo encontrado!").formatted(Formatting.RED), true);
            return;
        }

        List<StatusEffectInstance> positiveEffects = new ArrayList<>();
        
         for (StatusEffectInstance effect : target.getStatusEffects()) {
             if (effect.getEffectType().getCategory() == StatusEffectCategory.BENEFICIAL) {
                positiveEffects.add(new StatusEffectInstance(
                    effect.getEffectType(),
                    effect.getDuration(),
                    effect.getAmplifier(),
                    effect.isAmbient(),
                    effect.shouldShowParticles(),
                    effect.shouldShowIcon()
                ));
            }
        }

        if (positiveEffects.isEmpty()) {
            player.sendMessage(Text.literal("O alvo não possui efeitos positivos!").formatted(Formatting.YELLOW), true);
            return;
        }

        for (StatusEffectInstance effect : positiveEffects) {
            target.removeStatusEffect(effect.getEffectType());
            player.addStatusEffect(effect);
        }

        player.sendMessage(
            Text.literal(String.format("✦ Roubados %d efeito(s) positivo(s)!", positiveEffects.size()))
                .formatted(Formatting.LIGHT_PURPLE), 
            true
        );
    }

    private LivingEntity getLivingEntityLookedAt(ServerPlayerEntity player, double distance) {
        Vec3d eyePos = player.getCameraPosVec(1.0F);
        Vec3d lookVec = player.getRotationVec(1.0F);
        Vec3d targetPos = eyePos.add(lookVec.multiply(distance));

        Box box = player.getBoundingBox().stretch(lookVec.multiply(distance)).expand(1.0D, 1.0D, 1.0D);

        Predicate<Entity> filter = (entity) -> entity instanceof LivingEntity && entity != player && !entity.isSpectator();

        EntityHitResult entityHit = ProjectileUtil.raycast(
                player,
                eyePos,
                targetPos,
                box,
                filter,
                distance * distance
        );

        if (entityHit != null && entityHit.getEntity() instanceof LivingEntity targetEntity) {
            return targetEntity;
        }

        return null;
    }
}
