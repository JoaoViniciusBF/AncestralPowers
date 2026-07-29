package dev.joaq.ancestralpowers.mixin;

import dev.joaq.ancestralpowers.util.DownedStateTracker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityPoseMixin {

    @Inject(
        method = "getPose",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onGetPose(CallbackInfoReturnable<EntityPose> cir) {
        Entity entity = (Entity) (Object) this;
        
        if (entity instanceof PlayerEntity player) {
            if (DownedStateTracker.isDowned(player.getUuid())) {
                cir.setReturnValue(EntityPose.SWIMMING);
            }
        }
    }
}