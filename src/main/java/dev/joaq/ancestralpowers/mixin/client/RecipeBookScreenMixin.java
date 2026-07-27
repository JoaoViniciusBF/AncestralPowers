package dev.joaq.ancestralpowers.mixin.client;

import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.gui.screen.ingame.RecipeBookScreen")
public class RecipeBookScreenMixin {
    @Inject(method = "addRecipeBook", at = @At("HEAD"), cancellable = true)
    private void removeRecipeBook(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "onRecipeBookToggled", at = @At("HEAD"), cancellable = true)
    private void preventRecipeBookToggle(CallbackInfo ci) {
        ci.cancel();
    }
}
