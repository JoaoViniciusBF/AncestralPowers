package dev.joaq.ancestralpowers.mixin.client;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public class ScreenMixin {
    @Inject(method = "addDrawableChild", at = @At("HEAD"), cancellable = true)
    private <T extends Element & Drawable & Selectable> void removeRecipeBookButton(T drawableElement, CallbackInfoReturnable<T> cir) {
        if (drawableElement instanceof ToggleButtonWidget button && button.getClass() == ToggleButtonWidget.class && button.getWidth() == 20 && button.getHeight() == 18) {
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "addDrawableChild", at = @At("HEAD"), cancellable = true)
    private <T extends Element & Drawable & Selectable> void removeRecipeBookWidget(T drawableElement, CallbackInfoReturnable<T> cir) {
        if (drawableElement instanceof RecipeBookWidget || drawableElement instanceof ClickableWidget widget && widget.getClass().getName().contains("recipebook")) {
            cir.setReturnValue(null);
        }
    }
}
