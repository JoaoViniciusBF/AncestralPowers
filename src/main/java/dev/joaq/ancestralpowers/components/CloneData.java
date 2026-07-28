package dev.joaq.ancestralpowers.components;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CloneData {
    private final List<Clone> clones = new ArrayList<>();
    private int activeCloneIndex = -1;

    public static class Clone {
        public NbtCompound inventory;
        public NbtCompound enderChest;
        public float health;
        public int food;
        public float saturation;
        public int xpLevel;
        public float xpProgress;
        public BlockPos position;
        public String dimension;
        public NbtCompound effects;
        public UUID entityUuid;

        public Clone() {}

        public Clone(ServerPlayerEntity player) {
            this.inventory = new NbtCompound();
            NbtList inventoryList = new NbtList();
            player.getInventory().writeNbt(inventoryList);
            this.inventory.put("Inventory", inventoryList);
            
            this.enderChest = new NbtCompound();
            NbtList enderList = player.getEnderChestInventory().toNbtList();
            this.enderChest.put("EnderItems", enderList);

            this.health = player.getHealth();
            this.food = player.getHungerManager().getFoodLevel();
            this.saturation = player.getHungerManager().getSaturationLevel();
            this.xpLevel = player.experienceLevel;
            this.xpProgress = player.experienceProgress;
            this.position = player.getBlockPos();
            this.dimension = player.getWorld().getRegistryKey().getValue().toString();
            
            this.effects = new NbtCompound();
            NbtList effectsList = new NbtList();
            player.getActiveStatusEffects().forEach((effect, instance) -> {
                NbtCompound effectNbt = new NbtCompound();
                effectNbt.putString("id", effect.getTranslationKey());
                effectNbt.putInt("amplifier", instance.getAmplifier());
                effectNbt.putInt("duration", instance.getDuration());
                effectsList.add(effectNbt);
            });
            this.effects.put("effects", effectsList);
            
            this.entityUuid = null;
        }

        public NbtCompound toNbt() {
            NbtCompound nbt = new NbtCompound();
            nbt.put("inventory", inventory);
            nbt.put("enderChest", enderChest);
            nbt.putFloat("health", health);
            nbt.putInt("food", food);
            nbt.putFloat("saturation", saturation);
            nbt.putInt("xpLevel", xpLevel);
            nbt.putFloat("xpProgress", xpProgress);
            nbt.putInt("posX", position.getX());
            nbt.putInt("posY", position.getY());
            nbt.putInt("posZ", position.getZ());
            nbt.putString("dimension", dimension);
            nbt.put("effects", effects);
            if (entityUuid != null) {
                nbt.putUuid("entityUuid", entityUuid);
            }
            return nbt;
        }

        public static Clone fromNbt(NbtCompound nbt) {
            Clone clone = new Clone();
            clone.inventory = nbt.getCompound("inventory");
            clone.enderChest = nbt.getCompound("enderChest");
            clone.health = nbt.getFloat("health");
            clone.food = nbt.getInt("food");
            clone.saturation = nbt.getFloat("saturation");
            clone.xpLevel = nbt.getInt("xpLevel");
            clone.xpProgress = nbt.getFloat("xpProgress");
            clone.position = new BlockPos(nbt.getInt("posX"), nbt.getInt("posY"), nbt.getInt("posZ"));
            clone.dimension = nbt.getString("dimension");
            clone.effects = nbt.getCompound("effects");
            if (nbt.containsUuid("entityUuid")) {
                clone.entityUuid = nbt.getUuid("entityUuid");
            }
            return clone;
        }
    }

    public void addClone(Clone clone) {
        clones.add(clone);
    }

    public List<Clone> getClones() {
        return clones;
    }

    public int getActiveCloneIndex() {
        return activeCloneIndex;
    }

    public void setActiveCloneIndex(int index) {
        this.activeCloneIndex = index;
    }

    public Clone getClone(int index) {
        if (index >= 0 && index < clones.size()) {
            return clones.get(index);
        }
        return null;
    }

    public int getCloneCount() {
        return clones.size();
    }

    public void removeClone(int index) {
        if (index >= 0 && index < clones.size()) {
            clones.remove(index);
            if (activeCloneIndex >= clones.size()) {
                activeCloneIndex = clones.size() - 1;
            }
        }
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        NbtList clonesList = new NbtList();
        for (Clone clone : clones) {
            clonesList.add(clone.toNbt());
        }
        nbt.put("clones", clonesList);
        nbt.putInt("activeClone", activeCloneIndex);
        return nbt;
    }

    public static CloneData fromNbt(NbtCompound nbt) {
        CloneData data = new CloneData();
        NbtList clonesList = nbt.getList("clones", 10);
        for (int i = 0; i < clonesList.size(); i++) {
            data.addClone(Clone.fromNbt(clonesList.getCompound(i)));
        }
        data.activeCloneIndex = nbt.getInt("activeClone");
        return data;
    }

    public void clear() {
        clones.clear();
        activeCloneIndex = -1;
    }
}
