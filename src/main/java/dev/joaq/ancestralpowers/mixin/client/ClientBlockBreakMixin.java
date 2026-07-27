package dev.joaq.ancestralpowers.mixin.client;

import dev.joaq.ancestralpowers.networking.packet.c2s.OffhandAttackC2SPayload;
import dev.joaq.ancestralpowers.offhand.OffhandMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class ClientBlockBreakMixin {

    @Shadow @Final public HitResult crosshairTarget;
    @Shadow public ClientPlayerEntity player;
    @Shadow public ClientPlayerInteractionManager interactionManager;

    // Block left-click attack when offhand has weapon
    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void onDoAttack(CallbackInfoReturnable<Boolean> cir) {
        if (player == null || interactionManager == null) return;
        if (player.getOffHandStack().isEmpty()) return;
        if (!hasWeapon(player.getOffHandStack())) return;

        // Prevent attack if offhand is on cooldown
        if (!OffhandMod.canOffhandAttack(player)) {
            cir.setReturnValue(false);
            return;
        }

        // Check what's being targeted
        if (crosshairTarget instanceof BlockHitResult blockHit) {
            // Attack block with offhand
            interactionManager.attackBlock(blockHit.getBlockPos(), blockHit.getSide());
            player.swingHand(net.minecraft.util.Hand.OFF_HAND);
            OffhandMod.resetOffhandCooldown(player, player.getOffHandStack());
            cir.setReturnValue(false);
        } else if (crosshairTarget instanceof EntityHitResult) {
            // Entity attack via left-click - cancel to prevent main hand attack
            // User should use right-click for entity attacks with offhand
            cir.setReturnValue(false);
        }
    }

     private static boolean hasWeapon(net.minecraft.item.ItemStack stack) {
         var modifiers = stack.getAttributeModifiers(net.minecraft.entity.EquipmentSlot.MAINHAND);
         return !modifiers.get(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE).isEmpty();
     }
}