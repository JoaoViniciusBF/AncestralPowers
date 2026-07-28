package dev.joaq.ancestralpowers.corpse.gui;

import dev.joaq.ancestralpowers.corpse.Death;
import dev.joaq.ancestralpowers.corpse.entity.CorpseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;

import java.util.UUID;

public class CorpseGuis {

    public static void openCorpseGUI(ServerPlayerEntity player, CorpseEntity corpse) {
        openCorpseGUI(player, corpse, true, false);
    }

    public static void openCorpseGUI(ServerPlayerEntity player, CorpseEntity corpse, boolean editable, boolean history) {
        if (corpse.isMainInventoryEmpty() && !corpse.isEmpty()) {
            // Open additional items only
            player.openHandledScreen(new CorpseContainerProvider(corpse, editable, history));
        } else {
            player.openHandledScreen(new CorpseContainerProvider(corpse, editable, history));
        }
    }

    public static class CorpseContainerProvider implements NamedScreenHandlerFactory {
        private final CorpseEntity corpse;
        private final boolean editable;
        private final boolean history;

        public CorpseContainerProvider(CorpseEntity corpse, boolean editable, boolean history) {
            this.corpse = corpse;
            this.editable = editable;
            this.history = history;
        }

        @Override
        public Text getDisplayName() {
            return corpse.getDisplayName();
        }

        @Override
        public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
            return new CorpseScreenHandler(syncId, playerInventory, corpse.getDeath(), editable, history);
        }

        public CorpseEntity getCorpse() { return corpse; }
        public boolean isEditable() { return editable; }
        public boolean isHistory() { return history; }
    }

    public static class CorpseScreenHandler extends ScreenHandler {
        private final Death death;
        private final boolean editable;
        private final boolean history;
        private final Inventory inventory;

        // Constructor for ScreenHandlerType registration (client)
        public CorpseScreenHandler(int syncId, PlayerInventory playerInventory) {
            this(syncId, playerInventory, createEmptyDeath(), false, false);
        }

        private static Death createEmptyDeath() {
            Death d = new Death(new UUID(0L, 0L), new UUID(0L, 0L));
            return d;
        }

        public CorpseScreenHandler(int syncId, PlayerInventory playerInventory, Death death, boolean editable, boolean history) {
            super(CorpseHandledScreens.CORPSE_SCREEN_HANDLER, syncId);
            this.death = death;
            this.editable = editable;
            this.history = history;

            // Build combined inventory from death data
            DefaultedList<ItemStack> allItems = DefaultedList.ofSize(
                death.getMainInventory().size() + death.getArmorInventory().size() + death.getOffHandInventory().size(),
                ItemStack.EMPTY);
            
            int idx = 0;
            for (ItemStack s : death.getMainInventory()) allItems.set(idx++, s);
            for (ItemStack s : death.getArmorInventory()) allItems.set(idx++, s);
            for (ItemStack s : death.getOffHandInventory()) allItems.set(idx++, s);

            this.inventory = new SimpleInventory(allItems.toArray(new ItemStack[0]));

// Corpse inventory slots (main 36 slots = 4 rows x 9)
int slotIndex = 0;
for (int row = 0; row < 4; row++) {
    for (int col = 0; col < 9; col++) {
        addSlot(new Slot(inventory, slotIndex++, 8 + col * 18, 18 + row * 18));
    }
}
// Armor slots
for (int col = 0; col < 4; col++) {
    addSlot(new Slot(inventory, slotIndex++, 8 + col * 18, 102));
}
// Offhand
addSlot(new Slot(inventory, slotIndex++, 98, 102));

// Player inventory
for (int row = 0; row < 3; row++) {
    for (int col = 0; col < 9; col++) {
        addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 140 + row * 18));
    }
}
// Player hotbar
for (int col = 0; col < 9; col++) {
    addSlot(new Slot(playerInventory, col, 8 + col * 18, 198));
}
        }

        @Override
        public boolean canUse(PlayerEntity player) {
            return true;
        }

        @Override
        public ItemStack quickMove(PlayerEntity player, int slotIndex) {
            ItemStack newStack = ItemStack.EMPTY;
            Slot slot = slots.get(slotIndex);
            if (slot != null && slot.hasStack()) {
                ItemStack originalStack = slot.getStack();
                newStack = originalStack.copy();
                int inventoryEnd = death.getMainInventory().size() + death.getArmorInventory().size() + death.getOffHandInventory().size();
                
                if (slotIndex < inventoryEnd) {
                    if (!insertItem(originalStack, inventoryEnd, slots.size(), true)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!insertItem(originalStack, 0, inventoryEnd, false)) {
                    return ItemStack.EMPTY;
                }

                if (originalStack.isEmpty()) {
                    slot.setStack(ItemStack.EMPTY);
                } else {
                    slot.markDirty();
                }
            }
            return newStack;
        }

        public Death getDeath() { return death; }
        public boolean isEditable() { return editable; }
        public boolean isHistory() { return history; }
    }
}