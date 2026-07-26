package dev.joaq.ancestralpowers.mixin.client;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HandledScreen.class)
public interface InventoryScreenAccessor {
    @Accessor("backgroundWidth")
    @Mutable
    void setBackgroundWidth(int width);

    @Accessor("backgroundHeight")
    @Mutable
    void setBackgroundHeight(int height);

    @Accessor("x")
    @Mutable
    void setX(int x);

    @Accessor("y")
    @Mutable
    void setY(int y);
}
