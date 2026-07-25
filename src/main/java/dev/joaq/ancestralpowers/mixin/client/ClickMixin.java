package dev.joaq.ancestralpowers.mixin.client;

import dev.joaq.ancestralpowers.networking.packet.c2s.OffhandAttackC2SPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class ClickMixin {

    @Shadow @Final public HitResult crosshairTarget;
    @Shadow public ClientPlayerEntity player;
    @Shadow public ClientPlayerInteractionManager interactionManager;

    @Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
    private void onDoItemUse(CallbackInfo ci) {
        if (player == null || interactionManager == null) return;
        if (player.getOffHandStack().isEmpty()) return;
        if (!(crosshairTarget instanceof EntityHitResult entityHit)) return;
        if (!(entityHit.getEntity() instanceof net.minecraft.entity.LivingEntity)) return;
        if (!hasAttackDamage(player.getOffHandStack())) return;

        ClientPlayNetworking.send(new OffhandAttackC2SPayload());
        player.swingHand(Hand.OFF_HAND);
        interactionManager.attackEntity(player, entityHit.getEntity());
        ci.cancel();
    }

    private static boolean hasAttackDamage(net.minecraft.item.ItemStack stack) {
        var mods = stack.get(net.minecraft.component.DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (mods == null) return false;
        for (var entry : mods.modifiers()) {
            if (entry.attribute().value() == net.minecraft.entity.attribute.EntityAttributes.ATTACK_DAMAGE.value())
                return true;
        }
        return false;
    }
}
