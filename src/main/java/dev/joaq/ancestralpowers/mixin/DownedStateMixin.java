package dev.joaq.ancestralpowers.mixin;

import dev.joaq.ancestralpowers.components.MyComponents;
import dev.joaq.ancestralpowers.components.PlayerTraits;
import dev.joaq.ancestralpowers.networking.ModPacketsS2C;
import dev.joaq.ancestralpowers.util.DownedStateTracker;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class DownedStateMixin {

    private static final int BLEEDOUT_TICKS = 2000;
    private static final int TICK_INTERVAL = 20;

    @Inject(
        method = "damage",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (isKilling) return;

        LivingEntity entity = (LivingEntity) (Object) this;
        
        if (!(entity instanceof ServerPlayerEntity serverPlayer)) {
            return;
        }

        PlayerTraits traits = MyComponents.TRAITS.get(serverPlayer);
        
        if (traits.getIsDowned()) {
            cir.setReturnValue(false);
            return;
        }

        float healthAfterDamage = serverPlayer.getHealth() - amount;
        
        if (healthAfterDamage <= 0 && !serverPlayer.isCreative() && !serverPlayer.isSpectator()) {
            cir.setReturnValue(false);
            applyDownedState(serverPlayer, traits);
        }
    }

    @Inject(
        method = "tick",
        at = @At("TAIL")
    )
    private void onTick(CallbackInfo cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        
        if (!(entity instanceof ServerPlayerEntity serverPlayer)) {
            return;
        }

        PlayerTraits traits = MyComponents.TRAITS.get(serverPlayer);
        
        if (traits.getIsDowned()) {
            serverPlayer.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MOVEMENT_SPEED)
                .setBaseValue(0.05);

            serverPlayer.setHealth(1.0f);
            serverPlayer.getHungerManager().setFoodLevel(6);
            serverPlayer.getHungerManager().setSaturationLevel(0);
            serverPlayer.setAbsorptionAmount(0);

            int timer = traits.getBleedoutTimer();
            if (timer > 0) {
                if (timer % TICK_INTERVAL == 0) {
                    if (timer % 200 == 0 || timer <= 200) {
                        int seconds = timer / 20;
                        serverPlayer.sendMessage(
                            Text.literal("§cSangramento: " + seconds + "s restantes").formatted(Formatting.RED),
                            true
                        );
                    }
                    ModPacketsS2C.sendDownedStateSync(serverPlayer, true, timer);
                }
                traits.setBleedoutTimer(timer - 1);
            } else if (timer == 0) {
                traits.setBleedoutTimer(-1);
                killDownedPlayer(serverPlayer, traits);
            }
        }
    }

    @Unique
    private static boolean isKilling = false;

    private void applyDownedState(ServerPlayerEntity player, PlayerTraits traits) {
        DownedStateTracker.setDowned(player.getUuid(), true);
        traits.setIsDowned(true);
        traits.setBleedoutTimer(BLEEDOUT_TICKS);
        traits.setReviveProgress(0);
        traits.clearReviverUuid();

        player.setHealth(1.0f);
        player.setPose(EntityPose.SWIMMING);
        player.setVelocity(0, 0.1, 0);

        player.sendMessage(
            Text.literal("§c§lVOCÊ CAIU! §r§cSangramento: 100s - Aguarde resgate!").formatted(Formatting.RED),
            true
        );

        ServerWorld world = player.getServerWorld();
        world.getPlayers().forEach(p -> {
            if (p != player) {
                p.sendMessage(
                    Text.literal("§6" + player.getName().getString() + " §ecaiu e precisa de resgate!").formatted(Formatting.GOLD),
                    false
                );
            }
        });

        ModPacketsS2C.sendDownedStateSync(player, true, BLEEDOUT_TICKS);
    }

    private void killDownedPlayer(ServerPlayerEntity player, PlayerTraits traits) {
        DownedStateTracker.setDowned(player.getUuid(), false);
        traits.setIsDowned(false);
        traits.setBleedoutTimer(0);
        traits.clearReviverUuid();
        traits.setReviveProgress(0);

        player.setPose(EntityPose.STANDING);
        player.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MOVEMENT_SPEED)
            .setBaseValue(0.1);

        ModPacketsS2C.sendDownedStateSync(player, false, 0);

        isKilling = true;
        player.kill();
        isKilling = false;
    }
}