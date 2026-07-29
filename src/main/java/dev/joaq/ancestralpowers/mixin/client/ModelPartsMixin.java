package dev.joaq.ancestralpowers.mixin.client;

import dev.joaq.ancestralpowers.corpse.client.renderer.DummyPlayer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public class ModelPartsMixin {
    @Inject(method = "isModelPartShown", at = @At("RETURN"), cancellable = true)
    private void onIsModelPartShown(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof DummyPlayer) {
            cir.setReturnValue(true);
        }
    }
}