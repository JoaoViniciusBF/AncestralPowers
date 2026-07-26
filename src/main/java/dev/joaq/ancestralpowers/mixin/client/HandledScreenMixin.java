package dev.joaq.ancestralpowers.mixin.client;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> {

    @Shadow
    protected T handler;

    @Shadow
    protected int x;

    @Shadow
    protected int y;

    @Shadow
    protected int backgroundWidth;

    @Shadow
    protected int backgroundHeight;

    @Inject(method = "isClickOutsideBounds", at = @At("HEAD"), cancellable = true)
    private void fixArmorSlotClickBounds(double mouseX, double mouseY, int left, int top, int button, CallbackInfoReturnable<Boolean> cir) {
        HandledScreen<?> screen = (HandledScreen<?>) (Object) this;

        if (screen instanceof InventoryScreen && handler instanceof PlayerScreenHandler) {
            PlayerScreenHandler playerHandler = (PlayerScreenHandler) handler;

            int maxX = left + backgroundWidth;
            int maxY = top + backgroundHeight;

            for (Slot slot : playerHandler.slots) {
                int id = slot.id;
                if ((id >= 5 && id <= 8) || id == 81) {
                    int slotX = left + slot.x;
                    int slotY = top + slot.y;
                    int slotMaxX = slotX + 16;
                    int slotMaxY = slotY + 16;

                    if (slotMaxX > maxX) {
                        maxX = slotMaxX;
                    }
                    if (slotMaxY > maxY) {
                        maxY = slotMaxY;
                    }
                }
            }

            boolean isOutside = mouseX < left || mouseY < top || mouseX >= maxX || mouseY >= maxY;
            cir.setReturnValue(isOutside);
        }
    }
}
