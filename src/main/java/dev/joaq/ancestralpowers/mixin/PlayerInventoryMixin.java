package dev.joaq.ancestralpowers.mixin;

import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
    private static final int EXTRA_SLOTS = 36;

    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 36), require = 1)
    private int expandMainInventory(int original) {
        return original + EXTRA_SLOTS;
    }

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 36, ordinal = 0))
    private static int modifyFeetIndex(int original) {
        return original + EXTRA_SLOTS;
    }

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 36, ordinal = 1))
    private static int modifyLegsIndex(int original) {
        return original + EXTRA_SLOTS;
    }

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 36, ordinal = 2))
    private static int modifyChestIndex(int original) {
        return original + EXTRA_SLOTS;
    }

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 36, ordinal = 3))
    private static int modifyHeadIndex(int original) {
        return original + EXTRA_SLOTS;
    }

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 40))
    private static int modifyOffhandIndex(int original) {
        return original + EXTRA_SLOTS;
    }

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 41))
    private static int modifyBodyIndex(int original) {
        return original + EXTRA_SLOTS;
    }

    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 42))
    private static int modifySaddleIndex(int original) {
        return original + EXTRA_SLOTS;
    }

    @ModifyConstant(method = "getOccupiedSlotWithRoomForStack", constant = @Constant(intValue = 40), require = 0)
    private int modifyOffhandRoomCheck(int original) {
        return original + EXTRA_SLOTS;
    }
}
