package dev.joaq.ancestralpowers.mixin;

import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {
    @ModifyConstant(method = "addPlayerInventorySlots", constant = @Constant(intValue = 3))
    private int modifyInventoryRows(int original) {
        return 7;
    }

    @ModifyConstant(method = "addPlayerSlots", constant = @Constant(intValue = 58))
    private int modifyHotbarOffset(int original) {
        return 130;
    }

    @ModifyConstant(method = "internalOnSlotClick", constant = @Constant(intValue = 40), require = 0)
    private int modifyOffhandSwapKey(int original) {
        return original + 36;
    }
}