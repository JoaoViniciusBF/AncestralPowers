package dev.joaq.ancestralpowers.mixin.client;

import dev.joaq.ancestralpowers.client.InventoryLayoutConfig;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends HandledScreen<PlayerScreenHandler> {

    public InventoryScreenMixin(PlayerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void expandInventorySize(CallbackInfo ci) {
        this.backgroundWidth = 256;
        this.backgroundHeight = 238;
        this.y = (this.height - this.backgroundHeight) / 2;
        ((HandledScreenTitleAccessor) this).setTitleY(-10000);
        this.x = (this.width - this.backgroundWidth) / 2;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void modifySlotPositions(CallbackInfo ci) {
        InventoryScreen screen = (InventoryScreen) (Object) this;
        PlayerScreenHandler handler = (PlayerScreenHandler) ((HandledScreenAccessor) screen).getHandler();

        for (Slot slot : handler.slots) {
            int id = slot.id;

            if (id == 0) {
                ((SlotAccessor) slot).setX(InventoryLayoutConfig.craftingResultX);
                ((SlotAccessor) slot).setY(InventoryLayoutConfig.craftingResultY);
            } else if (id >= 1 && id <= 4) {
                int index = id - 1;
                int col = index % 2;
                int row = index / 2;
                ((SlotAccessor) slot).setX(InventoryLayoutConfig.craftingGridX + (col * 18));
                ((SlotAccessor) slot).setY(InventoryLayoutConfig.craftingGridY + (row * 18));
            } else if (id == 5) {
                ((SlotAccessor) slot).setX(InventoryLayoutConfig.helmetSlotX);
                ((SlotAccessor) slot).setY(InventoryLayoutConfig.helmetSlotY);
            } else if (id == 6) {
                ((SlotAccessor) slot).setX(InventoryLayoutConfig.chestplateSlotX);
                ((SlotAccessor) slot).setY(InventoryLayoutConfig.chestplateSlotY);
            } else if (id == 7) {
                ((SlotAccessor) slot).setX(InventoryLayoutConfig.leggingsSlotX);
                ((SlotAccessor) slot).setY(InventoryLayoutConfig.leggingsSlotY);
            } else if (id == 8) {
                ((SlotAccessor) slot).setX(InventoryLayoutConfig.bootsSlotX);
                ((SlotAccessor) slot).setY(InventoryLayoutConfig.bootsSlotY);
            } else if (id >= 9 && id <= 44) {
                int index = id - 9;
                int col = index % 9;
                int row = index / 9;
                ((SlotAccessor) slot).setX(8 + col * 18 + InventoryLayoutConfig.BASE_TEXTURE_SHIFT_X + InventoryLayoutConfig.inventoryOffsetX);
                ((SlotAccessor) slot).setY(12 + row * 18 + InventoryLayoutConfig.inventoryOffsetY);
            } else if (id >= 45 && id <= 71) {
                int index = id - 45;
                int col = index % 9;
                int row = index / 9;
                ((SlotAccessor) slot).setX(8 + col * 18 + InventoryLayoutConfig.BASE_TEXTURE_SHIFT_X + InventoryLayoutConfig.inventoryOffsetX);
                ((SlotAccessor) slot).setY(84 + row * 18 + InventoryLayoutConfig.inventoryOffsetY);
            } else if (id >= 72 && id <= 80) {
                int col = id - 72;
                ((SlotAccessor) slot).setX(8 + col * 18 + InventoryLayoutConfig.BASE_TEXTURE_SHIFT_X + InventoryLayoutConfig.inventoryOffsetX);
                ((SlotAccessor) slot).setY(142 + InventoryLayoutConfig.inventoryOffsetY);
            } else if (id == 81) {
                ((SlotAccessor) slot).setX(InventoryLayoutConfig.offhandSlotX);
                ((SlotAccessor) slot).setY(InventoryLayoutConfig.offhandSlotY);
            }
        }
    }

    @ModifyArg(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;drawEntity(Lnet/minecraft/client/gui/DrawContext;IIIIIFFFLnet/minecraft/entity/LivingEntity;)V"), index = 1)
    private int modifyPlayerRenderX(int x) {
        return this.x + InventoryLayoutConfig.playerRenderX;
    }

    @ModifyArg(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;drawEntity(Lnet/minecraft/client/gui/DrawContext;IIIIIFFFLnet/minecraft/entity/LivingEntity;)V"), index = 2)
    private int modifyPlayerRenderY(int y) {
        return this.y + InventoryLayoutConfig.playerRenderY;
    }

    @ModifyArg(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;drawEntity(Lnet/minecraft/client/gui/DrawContext;IIIIIFFFLnet/minecraft/entity/LivingEntity;)V"), index = 3)
    private int modifyPlayerRenderMaxX(int maxX) {
        return this.x + InventoryLayoutConfig.playerRenderX + 49;
    }

    @ModifyArg(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;drawEntity(Lnet/minecraft/client/gui/DrawContext;IIIIIFFFLnet/minecraft/entity/LivingEntity;)V"), index = 5)
    private int modifyPlayerRenderSize(int size) {
        return InventoryLayoutConfig.playerRenderSize;
    }
}