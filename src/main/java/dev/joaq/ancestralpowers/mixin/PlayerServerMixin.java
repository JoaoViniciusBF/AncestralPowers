package dev.joaq.ancestralpowers.mixin;

import dev.joaq.ancestralpowers.offhand.OffhandMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class PlayerServerMixin {

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void onAttack(net.minecraft.entity.Entity target, CallbackInfo ci) {
        ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;

        if (!OffhandMod.shouldOverride(self)) return;
        OffhandMod.setOverride(self, false);

        var offhand = self.getOffHandStack();
        if (offhand.isEmpty() || !hasAttackDamage(offhand)) return;
        if (!OffhandMod.canOffhandAttack(self)) return;

        var mainhand = self.getMainHandStack().copy();
        var offhandCopy = offhand.copy();

        self.setStackInHand(Hand.MAIN_HAND, offhandCopy);
        self.setStackInHand(Hand.OFF_HAND, mainhand);

        self.attack(target);

        self.setStackInHand(Hand.OFF_HAND, offhandCopy);
        self.setStackInHand(Hand.MAIN_HAND, mainhand);

        self.swingHand(Hand.OFF_HAND);
        OffhandMod.resetOffhandCooldown(self, offhand);

        ci.cancel();
    }

     private static boolean hasAttackDamage(net.minecraft.item.ItemStack stack) {
         var modifiers = stack.getAttributeModifiers(net.minecraft.entity.EquipmentSlot.MAINHAND);
         return !modifiers.get(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE).isEmpty();
     }
}
