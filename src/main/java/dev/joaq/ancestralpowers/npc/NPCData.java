package dev.joaq.ancestralpowers.npc;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class NPCData {
    private UUID npcId;
    private NPCType type;
    private UUID ownerUuid;
    private String ownerName;
    
    private String mainPower;
    private String secondaryPower;
    private String intelligence;
    
    private String skinName;
    private String skinValue;
    private String skinSignature;
    
    private DefaultedList<ItemStack> inventory;
    private DefaultedList<ItemStack> armor;
    private ItemStack offhand;
    
    private float health;
    private float maxHealth;
    private int foodLevel;
    private float saturation;
    
    private int xpLevel;
    private float xpProgress;
    private int totalXp;
    
    private BlockPos position;
    private String dimension;
    private float yaw;
    private float pitch;
    
    private List<StatusEffectInstance> effects;
    private UUID entityUuid;
    
    private long createdAt;
    private long lastControlledAt;
    
    private Map<String, String> customData;

    public NPCData() {
        this.npcId = UUID.randomUUID();
        this.type = NPCType.GENERIC;
        this.inventory = DefaultedList.ofSize(36, ItemStack.EMPTY);
        this.armor = DefaultedList.ofSize(4, ItemStack.EMPTY);
        this.offhand = ItemStack.EMPTY;
        this.effects = new ArrayList<>();
        this.customData = new HashMap<>();
        this.createdAt = System.currentTimeMillis();
        this.maxHealth = 20.0f;
        this.health = 20.0f;
    }

    public NPCData(ServerPlayerEntity player, NPCType type) {
        this();
        this.type = type;
        this.ownerUuid = player.getUuid();
        this.ownerName = player.getName().getString();
        
        this.health = player.getHealth();
        this.maxHealth = player.getMaxHealth();
        this.foodLevel = player.getHungerManager().getFoodLevel();
        this.saturation = player.getHungerManager().getSaturationLevel();
        
        this.xpLevel = player.experienceLevel;
        this.xpProgress = player.experienceProgress;
        this.totalXp = player.totalExperience;
        
        this.position = player.getBlockPos();
        this.dimension = player.getWorld().getRegistryKey().getValue().toString();
        this.yaw = player.getYaw();
        this.pitch = player.getPitch();
        
        for (int i = 0; i < player.getInventory().size(); i++) {
            if (i < this.inventory.size()) {
                this.inventory.set(i, player.getInventory().getStack(i).copy());
            }
        }
        
        for (int i = 0; i < 4; i++) {
            this.armor.set(i, player.getInventory().armor.get(i).copy());
        }
        
        this.offhand = player.getOffHandStack().copy();
        
        player.getActiveStatusEffects().forEach((effect, instance) -> {
            this.effects.add(new StatusEffectInstance(instance));
        });

        com.mojang.authlib.GameProfile profile = player.getGameProfile();
        if (profile.getProperties().containsKey("textures")) {
            com.mojang.authlib.properties.Property tex = profile.getProperties().get("textures").iterator().next();
            this.skinValue = tex.value();
            this.skinSignature = tex.signature();
            this.skinName = player.getName().getString();
        }
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        
        nbt.putUuid("npcId", npcId);
        nbt.putString("type", type.getId());
        
        if (ownerUuid != null) {
            nbt.putUuid("ownerUuid", ownerUuid);
        }
        if (ownerName != null) {
            nbt.putString("ownerName", ownerName);
        }
        
        if (mainPower != null) nbt.putString("mainPower", mainPower);
        if (secondaryPower != null) nbt.putString("secondaryPower", secondaryPower);
        if (intelligence != null) nbt.putString("intelligence", intelligence);
        
        if (skinName != null) nbt.putString("skinName", skinName);
        if (skinValue != null) nbt.putString("skinValue", skinValue);
        if (skinSignature != null) nbt.putString("skinSignature", skinSignature);
        
        NbtList inventoryList = new NbtList();
        for (int i = 0; i < inventory.size(); i++) {
            if (!inventory.get(i).isEmpty()) {
                NbtCompound itemNbt = new NbtCompound();
                itemNbt.putByte("Slot", (byte) i);
                inventory.get(i).writeNbt(itemNbt);
                inventoryList.add(itemNbt);
            }
        }
        nbt.put("Inventory", inventoryList);
        
        NbtList armorList = new NbtList();
        for (int i = 0; i < armor.size(); i++) {
            if (!armor.get(i).isEmpty()) {
                NbtCompound itemNbt = new NbtCompound();
                itemNbt.putByte("Slot", (byte) i);
                armor.get(i).writeNbt(itemNbt);
                armorList.add(itemNbt);
            }
        }
        nbt.put("Armor", armorList);
        
        if (!offhand.isEmpty()) {
            NbtCompound offhandNbt = new NbtCompound();
            offhand.writeNbt(offhandNbt);
            nbt.put("Offhand", offhandNbt);
        }
        
        nbt.putFloat("health", health);
        nbt.putFloat("maxHealth", maxHealth);
        nbt.putInt("foodLevel", foodLevel);
        nbt.putFloat("saturation", saturation);
        
        nbt.putInt("xpLevel", xpLevel);
        nbt.putFloat("xpProgress", xpProgress);
        nbt.putInt("totalXp", totalXp);
        
        if (position != null) {
            nbt.putInt("posX", position.getX());
            nbt.putInt("posY", position.getY());
            nbt.putInt("posZ", position.getZ());
        }
        if (dimension != null) {
            nbt.putString("dimension", dimension);
        }
        nbt.putFloat("yaw", yaw);
        nbt.putFloat("pitch", pitch);
        
        NbtList effectsList = new NbtList();
        for (StatusEffectInstance effect : effects) {
            NbtCompound effectNbt = new NbtCompound();
            effect.writeNbt(effectNbt);
            effectsList.add(effectNbt);
        }
        nbt.put("Effects", effectsList);
        
        if (entityUuid != null) {
            nbt.putUuid("entityUuid", entityUuid);
        }
        
        nbt.putLong("createdAt", createdAt);
        nbt.putLong("lastControlledAt", lastControlledAt);
        
        NbtCompound customDataNbt = new NbtCompound();
        customData.forEach(customDataNbt::putString);
        nbt.put("customData", customDataNbt);
        
        return nbt;
    }

    public static NPCData fromNbt(NbtCompound nbt) {
        NPCData data = new NPCData();
        
        data.npcId = nbt.getUuid("npcId");
        data.type = NPCType.fromString(nbt.getString("type"));
        
        if (nbt.containsUuid("ownerUuid")) {
            data.ownerUuid = nbt.getUuid("ownerUuid");
        }
        if (nbt.contains("ownerName")) {
            data.ownerName = nbt.getString("ownerName");
        }
        
        if (nbt.contains("mainPower")) data.mainPower = nbt.getString("mainPower");
        if (nbt.contains("secondaryPower")) data.secondaryPower = nbt.getString("secondaryPower");
        if (nbt.contains("intelligence")) data.intelligence = nbt.getString("intelligence");
        
        if (nbt.contains("skinName")) data.skinName = nbt.getString("skinName");
        if (nbt.contains("skinValue")) data.skinValue = nbt.getString("skinValue");
        if (nbt.contains("skinSignature")) data.skinSignature = nbt.getString("skinSignature");
        
        NbtList inventoryList = nbt.getList("Inventory", 10);
        for (int i = 0; i < inventoryList.size(); i++) {
            NbtCompound itemNbt = inventoryList.getCompound(i);
            int slot = itemNbt.getByte("Slot") & 255;
            if (slot < data.inventory.size()) {
                data.inventory.set(slot, ItemStack.fromNbt(itemNbt));
            }
        }
        
        NbtList armorList = nbt.getList("Armor", 10);
        for (int i = 0; i < armorList.size(); i++) {
            NbtCompound itemNbt = armorList.getCompound(i);
            int slot = itemNbt.getByte("Slot") & 255;
            if (slot < data.armor.size()) {
                data.armor.set(slot, ItemStack.fromNbt(itemNbt));
            }
        }
        
        if (nbt.contains("Offhand")) {
            data.offhand = ItemStack.fromNbt(nbt.getCompound("Offhand"));
        }
        
        data.health = nbt.getFloat("health");
        data.maxHealth = nbt.getFloat("maxHealth");
        data.foodLevel = nbt.getInt("foodLevel");
        data.saturation = nbt.getFloat("saturation");
        
        data.xpLevel = nbt.getInt("xpLevel");
        data.xpProgress = nbt.getFloat("xpProgress");
        data.totalXp = nbt.getInt("totalXp");
        
        if (nbt.contains("posX")) {
            data.position = new BlockPos(
                nbt.getInt("posX"),
                nbt.getInt("posY"),
                nbt.getInt("posZ")
            );
        }
        if (nbt.contains("dimension")) {
            data.dimension = nbt.getString("dimension");
        }
        data.yaw = nbt.getFloat("yaw");
        data.pitch = nbt.getFloat("pitch");
        
        NbtList effectsList = nbt.getList("Effects", 10);
        data.effects.clear();
        for (int i = 0; i < effectsList.size(); i++) {
            StatusEffectInstance effect = StatusEffectInstance.fromNbt(effectsList.getCompound(i));
            if (effect != null) {
                data.effects.add(effect);
            }
        }
        
        if (nbt.containsUuid("entityUuid")) {
            data.entityUuid = nbt.getUuid("entityUuid");
        }
        
        data.createdAt = nbt.getLong("createdAt");
        data.lastControlledAt = nbt.getLong("lastControlledAt");
        
        if (nbt.contains("customData")) {
            NbtCompound customDataNbt = nbt.getCompound("customData");
            for (String key : customDataNbt.getKeys()) {
                data.customData.put(key, customDataNbt.getString(key));
            }
        }
        
        return data;
    }

    public UUID getNpcId() { return npcId; }
    public void setNpcId(UUID npcId) { this.npcId = npcId; }
    
    public NPCType getType() { return type; }
    public void setType(NPCType type) { this.type = type; }
    
    public UUID getOwnerUuid() { return ownerUuid; }
    public void setOwnerUuid(UUID ownerUuid) { this.ownerUuid = ownerUuid; }
    
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    
    public String getMainPower() { return mainPower; }
    public void setMainPower(String mainPower) { this.mainPower = mainPower; }
    
    public String getSecondaryPower() { return secondaryPower; }
    public void setSecondaryPower(String secondaryPower) { this.secondaryPower = secondaryPower; }
    
    public String getIntelligence() { return intelligence; }
    public void setIntelligence(String intelligence) { this.intelligence = intelligence; }
    
    public String getSkinName() { return skinName; }
    public void setSkinName(String skinName) { this.skinName = skinName; }
    
    public String getSkinValue() { return skinValue; }
    public void setSkinValue(String skinValue) { this.skinValue = skinValue; }
    
    public String getSkinSignature() { return skinSignature; }
    public void setSkinSignature(String skinSignature) { this.skinSignature = skinSignature; }
    
    public DefaultedList<ItemStack> getInventory() { return inventory; }
    public void setInventory(DefaultedList<ItemStack> inventory) { this.inventory = inventory; }
    
    public DefaultedList<ItemStack> getArmor() { return armor; }
    public void setArmor(DefaultedList<ItemStack> armor) { this.armor = armor; }
    
    public ItemStack getOffhand() { return offhand; }
    public void setOffhand(ItemStack offhand) { this.offhand = offhand; }
    
    public float getHealth() { return health; }
    public void setHealth(float health) { this.health = health; }
    
    public float getMaxHealth() { return maxHealth; }
    public void setMaxHealth(float maxHealth) { this.maxHealth = maxHealth; }
    
    public int getFoodLevel() { return foodLevel; }
    public void setFoodLevel(int foodLevel) { this.foodLevel = foodLevel; }
    
    public float getSaturation() { return saturation; }
    public void setSaturation(float saturation) { this.saturation = saturation; }
    
    public int getXpLevel() { return xpLevel; }
    public void setXpLevel(int xpLevel) { this.xpLevel = xpLevel; }
    
    public float getXpProgress() { return xpProgress; }
    public void setXpProgress(float xpProgress) { this.xpProgress = xpProgress; }
    
    public int getTotalXp() { return totalXp; }
    public void setTotalXp(int totalXp) { this.totalXp = totalXp; }
    
    public BlockPos getPosition() { return position; }
    public void setPosition(BlockPos position) { this.position = position; }
    
    public String getDimension() { return dimension; }
    public void setDimension(String dimension) { this.dimension = dimension; }
    
    public float getYaw() { return yaw; }
    public void setYaw(float yaw) { this.yaw = yaw; }
    
    public float getPitch() { return pitch; }
    public void setPitch(float pitch) { this.pitch = pitch; }
    
    public List<StatusEffectInstance> getEffects() { return effects; }
    public void setEffects(List<StatusEffectInstance> effects) { this.effects = effects; }
    
    public UUID getEntityUuid() { return entityUuid; }
    public void setEntityUuid(UUID entityUuid) { this.entityUuid = entityUuid; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    public long getLastControlledAt() { return lastControlledAt; }
    public void setLastControlledAt(long lastControlledAt) { this.lastControlledAt = lastControlledAt; }
    
    public Map<String, String> getCustomData() { return customData; }
    public void setCustomData(Map<String, String> customData) { this.customData = customData; }
    
    public void setCustomValue(String key, String value) {
        customData.put(key, value);
    }
    
    public String getCustomValue(String key) {
        return customData.get(key);
    }
}
