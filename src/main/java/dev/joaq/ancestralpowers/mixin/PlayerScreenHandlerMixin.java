package dev.joaq.ancestralpowers.mixin;

import net.minecraft.screen.PlayerScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PlayerScreenHandler.class)
public abstract class PlayerScreenHandlerMixin {
    private static final int EXTRA_SLOTS = 36;

    @ModifyConstant(method = {"<clinit>", "isInHotbar", "quickMove"}, constant = @Constant(intValue = 36), require = 1)
    private static int modifyHotbarStart(int original) {
        return original + EXTRA_SLOTS;
    }

    @ModifyConstant(method = {"<clinit>", "isInHotbar", "quickMove"}, constant = @Constant(intValue = 45), require = 1)
    private static int modifyHotbarEnd(int original) {
        return original + EXTRA_SLOTS;
    }

    @ModifyConstant(method = {"<clinit>", "isInHotbar", "quickMove"}, constant = @Constant(intValue = 46), require = 1)
    private static int modifyOffhandEnd(int original) {
        return original + EXTRA_SLOTS;
    }

    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 39))
    private int modifyArmorSlotIndex(int original) {
        return original + EXTRA_SLOTS;
    }

    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 40))
    private int modifyOffhandSlotIndex(int original) {
        return original + EXTRA_SLOTS;
    }
}