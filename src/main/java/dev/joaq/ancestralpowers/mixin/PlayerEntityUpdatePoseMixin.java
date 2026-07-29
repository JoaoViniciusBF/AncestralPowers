package dev.joaq.ancestralpowers.mixin;

import dev.joaq.ancestralpowers.util.DownedStateTracker;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityUpdatePoseMixin {

    @Inject(
        method = "updatePose",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onUpdatePose(CallbackInfo cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        
        if (DownedStateTracker.isDowned(player.getUuid())) {
            cir.cancel();
        }
    }
}