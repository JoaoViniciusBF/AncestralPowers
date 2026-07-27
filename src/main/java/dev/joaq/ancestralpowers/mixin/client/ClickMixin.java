package dev.joaq.ancestralpowers.mixin.client;

import dev.joaq.ancestralpowers.networking.packet.c2s.OffhandAttackC2SPayload;
import dev.joaq.ancestralpowers.offhand.OffhandMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
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

    @Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
    private void onDoItemUse(CallbackInfo ci) {
        if (player == null) return;
        if (player.getOffHandStack().isEmpty()) return;
        if (!hasAttackDamage(player.getOffHandStack())) return;

        // Only handle entities via right-click
        if (!(crosshairTarget instanceof EntityHitResult entityHit)) return;
        if (!(entityHit.getEntity() instanceof net.minecraft.entity.LivingEntity)) return;

        // Check if offhand cooldown is ready - prevent rapid clicking
        if (!OffhandMod.canOffhandAttack(player)) {
             ci.cancel();
             return;
         }

         net.minecraft.network.PacketByteBuf buf = net.fabricmc.fabric.api.networking.v1.PacketByteBufs.create();
         OffhandAttackC2SPayload.write(buf, entityHit.getEntity().getId());
         ClientPlayNetworking.send(OffhandAttackC2SPayload.ID, buf);
         
         player.swingHand(Hand.OFF_HAND);
         
         OffhandMod.resetOffhandCooldown(player, player.getOffHandStack());
         
         ci.cancel();
    }

    private static boolean hasAttackDamage(net.minecraft.item.ItemStack stack) {
         var modifiers = stack.getAttributeModifiers(net.minecraft.entity.EquipmentSlot.MAINHAND);
         return !modifiers.get(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE).isEmpty();
     }
}