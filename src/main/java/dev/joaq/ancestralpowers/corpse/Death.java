package dev.joaq.ancestralpowers.corpse;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;

import java.util.UUID;

public class Death {

    private UUID playerUUID;
    private UUID deathUUID;
    private String playerName;
    private DefaultedList<ItemStack> mainInventory;
    private DefaultedList<ItemStack> armorInventory;
    private DefaultedList<ItemStack> offHandInventory;
    private DefaultedList<ItemStack> additionalItems;
    private DefaultedList<ItemStack> equipment;
    private double posX, posY, posZ;
    private float yaw, pitch;
    private byte model;
    private String dimension;
    private int xpLevel;
    private float xpProgress;
    private int totalXp;
    private int foodLevel;
    private float saturation;
    private float health;
    private long timestamp;

    private Death() {
    }

    public Death(UUID playerUUID, UUID deathUUID) {
        this.playerUUID = playerUUID;
        this.deathUUID = deathUUID;
        this.mainInventory = DefaultedList.ofSize(36, ItemStack.EMPTY);
        this.armorInventory = DefaultedList.ofSize(4, ItemStack.EMPTY);
        this.offHandInventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
        this.additionalItems = DefaultedList.ofSize(0, ItemStack.EMPTY);
        this.equipment = DefaultedList.ofSize(EquipmentSlot.values().length, ItemStack.EMPTY);
        this.timestamp = System.currentTimeMillis();
    }

    public NbtCompound toNbt() {
        return toNbt(true);
    }

    public NbtCompound toNbt(boolean includeInventory) {
        NbtCompound nbt = new NbtCompound();
        nbt.putUuid("PlayerUUID", playerUUID);
        nbt.putUuid("DeathUUID", deathUUID);
        if (playerName != null) nbt.putString("PlayerName", playerName);
        nbt.putDouble("PosX", posX);
        nbt.putDouble("PosY", posY);
        nbt.putDouble("PosZ", posZ);
        nbt.putFloat("Yaw", yaw);
        nbt.putFloat("Pitch", pitch);
        nbt.putByte("Model", model);
        if (dimension != null) nbt.putString("Dimension", dimension);
        nbt.putInt("XpLevel", xpLevel);
        nbt.putFloat("XpProgress", xpProgress);
        nbt.putInt("TotalXp", totalXp);
        nbt.putInt("FoodLevel", foodLevel);
        nbt.putFloat("Saturation", saturation);
        nbt.putFloat("Health", health);
        nbt.putLong("Timestamp", timestamp);

        if (includeInventory) {
            nbt.put("MainInventory", writeItemList(mainInventory));
            nbt.put("ArmorInventory", writeItemList(armorInventory));
            nbt.put("OffHandInventory", writeItemList(offHandInventory));
            nbt.put("AdditionalItems", writeItemList(additionalItems));
            nbt.put("Equipment", writeEquipment(equipment));
        }

        return nbt;
    }

    public static Death fromNbt(NbtCompound nbt) {
        Death death = new Death();
        death.playerUUID = nbt.getUuid("PlayerUUID");
        death.deathUUID = nbt.getUuid("DeathUUID");
        death.playerName = nbt.contains("PlayerName") ? nbt.getString("PlayerName") : null;
        death.posX = nbt.getDouble("PosX");
        death.posY = nbt.getDouble("PosY");
        death.posZ = nbt.getDouble("PosZ");
        death.yaw = nbt.getFloat("Yaw");
        death.pitch = nbt.getFloat("Pitch");
        death.model = nbt.getByte("Model");
        death.dimension = nbt.contains("Dimension") ? nbt.getString("Dimension") : null;
        death.xpLevel = nbt.getInt("XpLevel");
        death.xpProgress = nbt.getFloat("XpProgress");
        death.totalXp = nbt.getInt("TotalXp");
        death.foodLevel = nbt.getInt("FoodLevel");
        death.saturation = nbt.getFloat("Saturation");
        death.health = nbt.getFloat("Health");
        death.timestamp = nbt.getLong("Timestamp");

        death.mainInventory = readItemList(nbt.getList("MainInventory", 10), 36);
        death.armorInventory = readItemList(nbt.getList("ArmorInventory", 10), 4);
        death.offHandInventory = readItemList(nbt.getList("OffHandInventory", 10), 1);
        death.additionalItems = readItemList(nbt.getList("AdditionalItems", 10), 0);
        death.equipment = readEquipment(nbt.getList("Equipment", 10));

        return death;
    }

    private static NbtList writeItemList(DefaultedList<ItemStack> items) {
        NbtList list = new NbtList();
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (!stack.isEmpty()) {
                NbtCompound itemNbt = new NbtCompound();
                itemNbt.putByte("Slot", (byte) i);
                stack.writeNbt(itemNbt);
                list.add(itemNbt);
            }
        }
        return list;
    }

    private static DefaultedList<ItemStack> readItemList(NbtList list, int size) {
        DefaultedList<ItemStack> items = DefaultedList.ofSize(size > 0 ? size : list.size(), ItemStack.EMPTY);
        for (int i = 0; i < list.size(); i++) {
            NbtCompound itemNbt = list.getCompound(i);
            int slot = itemNbt.getByte("Slot") & 255;
            if (slot < items.size()) {
                items.set(slot, ItemStack.fromNbt(itemNbt));
            }
        }
        return items;
    }

    private static NbtList writeEquipment(DefaultedList<ItemStack> equipment) {
        NbtList list = new NbtList();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = equipment.get(slot.ordinal());
            if (!stack.isEmpty()) {
                NbtCompound itemNbt = new NbtCompound();
                itemNbt.putByte("Slot", (byte) slot.ordinal());
                stack.writeNbt(itemNbt);
                list.add(itemNbt);
            }
        }
        return list;
    }

    private static DefaultedList<ItemStack> readEquipment(NbtList list) {
        DefaultedList<ItemStack> equipment = DefaultedList.ofSize(EquipmentSlot.values().length, ItemStack.EMPTY);
        for (int i = 0; i < list.size(); i++) {
            NbtCompound itemNbt = list.getCompound(i);
            int slot = itemNbt.getByte("Slot") & 255;
            if (slot < equipment.size()) {
                equipment.set(slot, ItemStack.fromNbt(itemNbt));
            }
        }
        return equipment;
    }

    public UUID getPlayerUUID() { return playerUUID; }
    public UUID getDeathUUID() { return deathUUID; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public DefaultedList<ItemStack> getMainInventory() { return mainInventory; }
    public DefaultedList<ItemStack> getArmorInventory() { return armorInventory; }
    public DefaultedList<ItemStack> getOffHandInventory() { return offHandInventory; }
    public DefaultedList<ItemStack> getAdditionalItems() { return additionalItems; }
    public DefaultedList<ItemStack> getEquipment() { return equipment; }
    public void setEquipment(DefaultedList<ItemStack> equipment) { this.equipment = equipment; }

    public DefaultedList<ItemStack> getAllItems() {
        DefaultedList<ItemStack> all = DefaultedList.ofSize(
            mainInventory.size() + armorInventory.size() + offHandInventory.size() + additionalItems.size() + equipment.size(),
            ItemStack.EMPTY);
        int idx = 0;
        for (ItemStack s : mainInventory) all.set(idx++, s);
        for (ItemStack s : armorInventory) all.set(idx++, s);
        for (ItemStack s : offHandInventory) all.set(idx++, s);
        for (ItemStack s : additionalItems) all.set(idx++, s);
        for (ItemStack s : equipment) all.set(idx++, s);
        return all;
    }

    public double getPosX() { return posX; }
    public void setPosX(double posX) { this.posX = posX; }
    public double getPosY() { return posY; }
    public void setPosY(double posY) { this.posY = posY; }
    public double getPosZ() { return posZ; }
    public void setPosZ(double posZ) { this.posZ = posZ; }
    public float getYaw() { return yaw; }
    public void setYaw(float yaw) { this.yaw = yaw; }
    public float getPitch() { return pitch; }
    public void setPitch(float pitch) { this.pitch = pitch; }
    public byte getModel() { return model; }
    public void setModel(byte model) { this.model = model; }
    public String getDimension() { return dimension; }
    public void setDimension(String dimension) { this.dimension = dimension; }
    public int getXpLevel() { return xpLevel; }
    public void setXpLevel(int xpLevel) { this.xpLevel = xpLevel; }
    public float getXpProgress() { return xpProgress; }
    public void setXpProgress(float xpProgress) { this.xpProgress = xpProgress; }
    public int getTotalXp() { return totalXp; }
    public void setTotalXp(int totalXp) { this.totalXp = totalXp; }
    public int getFoodLevel() { return foodLevel; }
    public void setFoodLevel(int foodLevel) { this.foodLevel = foodLevel; }
    public float getSaturation() { return saturation; }
    public void setSaturation(float saturation) { this.saturation = saturation; }
    public float getHealth() { return health; }
    public void setHealth(float health) { this.health = health; }
    public long getTimestamp() { return timestamp; }

    public static class Builder {
        private final Death death;
        public Builder(UUID playerUUID, UUID deathUUID) {
            death = new Death(playerUUID, deathUUID);
        }
        public Builder playerName(String name) { death.playerName = name; return this; }
        public Builder mainInventory(DefaultedList<ItemStack> inv) { death.mainInventory = inv; return this; }
        public Builder armorInventory(DefaultedList<ItemStack> inv) { death.armorInventory = inv; return this; }
        public Builder offHandInventory(DefaultedList<ItemStack> inv) { death.offHandInventory = inv; return this; }
        public Builder additionalItems(DefaultedList<ItemStack> items) { death.additionalItems = items; return this; }
        public Builder equipment(DefaultedList<ItemStack> eq) { death.equipment = eq; return this; }
        public Builder pos(double x, double y, double z) { death.posX = x; death.posY = y; death.posZ = z; return this; }
        public Builder yaw(float yaw) { death.yaw = yaw; return this; }
        public Builder pitch(float pitch) { death.pitch = pitch; return this; }
        public Builder model(byte model) { death.model = model; return this; }
        public Builder dimension(String dim) { death.dimension = dim; return this; }
        public Builder xp(int level, float progress, int total) { death.xpLevel = level; death.xpProgress = progress; death.totalXp = total; return this; }
        public Builder food(int level, float saturation) { death.foodLevel = level; death.saturation = saturation; return this; }
        public Builder health(float health) { death.health = health; return this; }
        public Death build() { return death; }
    }
}