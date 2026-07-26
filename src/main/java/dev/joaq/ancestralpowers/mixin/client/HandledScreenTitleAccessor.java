package dev.joaq.ancestralpowers.mixin.client;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HandledScreen.class)
public interface HandledScreenTitleAccessor {
    @Accessor("titleY")
    @Mutable
    void setTitleY(int titleY);
}
