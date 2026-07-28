package dev.joaq.ancestralpowers.corpse;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;

import java.util.UUID;

public class DeathManager {

    public static void deleteOldDeaths(net.minecraft.server.world.ServerWorld world) {
        int ageInDays = CorpseConfig.maxDeathAge;
        if (ageInDays < 0) return;
        long ageInMillis = ((long) ageInDays) * 24L * 60L * 60L * 1000L;
        // Death storage not fully implemented - placeholder for cleanup
    }

    public static Death getDeath(net.minecraft.server.world.ServerWorld world, UUID playerUUID, UUID deathUUID) {
        // Death storage not fully implemented - placeholder
        return null;
    }

    public static Death createDeathFromPlayer(ServerPlayerEntity player) {
        Death.Builder builder = new Death.Builder(player.getUuid(), UUID.randomUUID())
                .playerName(player.getName().getString())
                .pos(player.getX(), player.getY(), player.getZ())
                .yaw(player.getYaw())
                .pitch(player.getPitch())
                .dimension(player.getWorld().getRegistryKey().getValue().toString())
                .xp(player.experienceLevel, player.experienceProgress, player.totalExperience)
                .food(player.getHungerManager().getFoodLevel(), player.getHungerManager().getSaturationLevel())
                .health(player.getHealth());

        // Store main inventory
        PlayerInventory inv = player.getInventory();
        DefaultedList<ItemStack> mainInv = DefaultedList.ofSize(36, ItemStack.EMPTY);
        int itemCount = 0;
        for (int i = 0; i < 36; i++) {
            mainInv.set(i, inv.getStack(i).copy());
            if (!mainInv.get(i).isEmpty()) itemCount++;
        }
        System.out.println("[Corpse] Main inventory items: " + itemCount);
        builder.mainInventory(mainInv);

        // Store armor
        DefaultedList<ItemStack> armor = DefaultedList.ofSize(4, ItemStack.EMPTY);
        int armorCount = 0;
        for (int i = 0; i < 4; i++) {
            armor.set(i, inv.armor.get(i).copy());
            if (!armor.get(i).isEmpty()) armorCount++;
        }
        System.out.println("[Corpse] Armor items: " + armorCount);
        builder.armorInventory(armor);

        // Store offhand
        DefaultedList<ItemStack> offhand = DefaultedList.ofSize(1, ItemStack.EMPTY);
        offhand.set(0, inv.offHand.get(0).copy());
        System.out.println("[Corpse] Offhand: " + (offhand.get(0).isEmpty() ? "empty" : offhand.get(0).getItem().toString()));
        builder.offHandInventory(offhand);

        // Store equipment
        DefaultedList<ItemStack> equipment = DefaultedList.ofSize(EquipmentSlot.values().length, ItemStack.EMPTY);
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            equipment.set(slot.ordinal(), player.getEquippedStack(slot).copy());
        }
        builder.equipment(equipment);

        // Model parts - enable all for corpse display
        // In modern MC, model parts are stored as Set<PlayerModelPart>
        // The bitmask values are: CAPE=1, JACKET=2, LEFT_SLEEVE=4, RIGHT_SLEEVE=8,
        // LEFT_PANTS_LEG=16, RIGHT_PANTS_LEG=32, HAT=64 = 0x7F = 127
        byte model = 0x7F;
        builder.model(model);

        return builder.build();
    }

    public static void removeDeath(net.minecraft.server.world.ServerWorld world, UUID playerUUID, UUID deathUUID) {
        // Death storage not fully implemented - placeholder
    }
}