package dev.joaq.ancestralpowers.corpse.entity;

import dev.joaq.ancestralpowers.corpse.CorpseConfig;
import dev.joaq.ancestralpowers.corpse.Death;
import dev.joaq.ancestralpowers.corpse.gui.CorpseGuis;
import dev.joaq.ancestralpowers.registry.ModCorpseEntities;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.UUID;

public class CorpseEntity extends CorpseBoundingBoxBase {

    private static final int NO_EMPTY_AGE = -1;

    // Synced data: UUID and name of the dead player (used by client to fetch skin)
    private static final TrackedData<Optional<UUID>> CORPSE_UUID = DataTracker.registerData(CorpseEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final TrackedData<String> CORPSE_NAME = DataTracker.registerData(CorpseEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Boolean> IS_SKELETON = DataTracker.registerData(CorpseEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Byte> CORPSE_MODEL = DataTracker.registerData(CorpseEntity.class, TrackedDataHandlerRegistry.BYTE);
    // Equipment stored as JSON string in a String tracker
    private static final TrackedData<String> EQUIPMENT_JSON = DataTracker.registerData(CorpseEntity.class, TrackedDataHandlerRegistry.STRING);
    // Skin data synced via DataTracker so client can render correct skin
    private static final TrackedData<String> SKIN_VALUE = DataTracker.registerData(CorpseEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<String> SKIN_SIGNATURE = DataTracker.registerData(CorpseEntity.class, TrackedDataHandlerRegistry.STRING);

    private int age;
    private int emptyAge;

    protected Death death;

    public CorpseEntity(EntityType<?> type, World world) {
        super(type, world);
        this.emptyAge = NO_EMPTY_AGE;
        this.death = new Death(new UUID(0L, 0L), new UUID(0L, 0L));
    }

    public CorpseEntity(World world) {
        this(ModCorpseEntities.CORPSE_ENTITY, world);
    }

    public static CorpseEntity createFromDeath(PlayerEntity player, Death death) {
        CorpseEntity corpse = new CorpseEntity(player.getWorld());
        corpse.death = death;
        corpse.setCorpseUUID(death.getPlayerUUID());
        corpse.setCorpseName(death.getPlayerName());
        corpse.setEquipmentFromPlayer(player);
        corpse.setYaw(player.getYaw());
        corpse.setPosition(death.getPosX(), Math.max(death.getPosY(), player.getWorld().getBottomY()), death.getPosZ());
        corpse.setCorpseModel(getModelParts(player));
        // Sync skin data to client
        com.mojang.authlib.GameProfile profile = player.getGameProfile();
        if (profile.getProperties().containsKey("textures")) {
            com.mojang.authlib.properties.Property tex = profile.getProperties().get("textures").iterator().next();
            String val = tex.value();
            String sig = tex.signature();
            if (val != null && !val.isEmpty()) corpse.dataTracker.set(SKIN_VALUE, val);
            if (sig != null && !sig.isEmpty()) corpse.dataTracker.set(SKIN_SIGNATURE, sig);
        }
        return corpse;
    }

private static byte getModelParts(PlayerEntity player) {
        byte model = 0;
        // Model parts: CAPE=1, JACKET=2, LEFT_SLEEVE=4, RIGHT_SLEEVE=8, LEFT_PANTS_LEG=16, RIGHT_PANTS_LEG=32, HAT=64
        try {
            Class<?> modelPartClass = Class.forName("net.minecraft.entity.player.PlayerEntity$ModelPart");
            for (Object part : modelPartClass.getEnumConstants()) {
                java.lang.reflect.Method isShown = PlayerEntity.class.getMethod("isModelPartShown", part.getClass());
                Boolean shown = (Boolean) isShown.invoke(player, part);
                if (Boolean.TRUE.equals(shown)) {
                    int mask = ((Enum<?>) part).ordinal();
                    if (mask < 7) model |= (1 << mask);
                }
            }
        } catch (Exception e) {
            // Fallback - no model parts
        }
        return model;
    }

    private void setEquipmentFromPlayer(PlayerEntity player) {
        NbtList equipmentList = new NbtList();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getEquippedStack(slot);
            if (!stack.isEmpty()) {
                NbtCompound itemNbt = new NbtCompound();
                itemNbt.putByte("Slot", (byte) slot.getEntitySlotId());
                stack.writeNbt(itemNbt);
                equipmentList.add(itemNbt);
            }
        }
        this.dataTracker.set(EQUIPMENT_JSON, equipmentList.toString());
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(CORPSE_UUID, Optional.empty());
        this.dataTracker.startTracking(CORPSE_NAME, "");
        this.dataTracker.startTracking(IS_SKELETON, false);
        this.dataTracker.startTracking(CORPSE_MODEL, (byte) 0);
        this.dataTracker.startTracking(EQUIPMENT_JSON, "");
        this.dataTracker.startTracking(SKIN_VALUE, "");
        this.dataTracker.startTracking(SKIN_SIGNATURE, "");
    }

    @Override
    protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.35F;
    }

    @Override
    public void tick() {
        super.tick();

        if (!hasNoGravity()) {
            double yMotion = 0D;
            Vec3d motion = getVelocity();
            if (isTouchingWater() || isInLava()) {
                if (motion.y < 0D) {
                    yMotion = motion.y + (motion.y < 0.03D ? 0.01D : 0D);
                } else {
                    yMotion = motion.y + (motion.y < 0.03D ? 5E-4D : 0D);
                }
            } else if (CorpseConfig.fallIntoVoid || getY() > getWorld().getBottomY()) {
                yMotion = Math.max(-2D, motion.y - 0.0625D);
            }
            setVelocity(getVelocity().x * 0.75D, yMotion, getVelocity().z * 0.75D);

            if (!CorpseConfig.fallIntoVoid && getY() < getWorld().getBottomY()) {
                teleport(getX(), getWorld().getBottomY(), getZ());
            }

            move(MovementType.SELF, getVelocity());
        }

        if (getWorld().isClient) {
            return;
        }

        age++;
        setSkeleton(CorpseConfig.corpseSkeletonTime > 0 && age >= CorpseConfig.corpseSkeletonTime);

        if (CorpseConfig.corpseForceDespawnTime > 0 && age > CorpseConfig.corpseForceDespawnTime) {
            discard();
            return;
        }

        boolean empty = isEmpty();
        if (empty && emptyAge < 0) {
            emptyAge = age;
        } else if (empty && age - emptyAge >= CorpseConfig.corpseDespawnTime) {
            discard();
        }
    }

    public boolean isMainInventoryEmpty() {
        return death.getMainInventory().stream().allMatch(ItemStack::isEmpty)
                && death.getArmorInventory().stream().allMatch(ItemStack::isEmpty)
                && death.getOffHandInventory().stream().allMatch(ItemStack::isEmpty);
    }

    public boolean isAdditionalInventoryEmpty() {
        return death.getAdditionalItems().stream().allMatch(ItemStack::isEmpty);
    }

    public boolean isEmpty() {
        return isMainInventoryEmpty() && isAdditionalInventoryEmpty();
    }

    @Override
    public boolean damage(net.minecraft.entity.damage.DamageSource source, float amount) {
        if (CorpseConfig.lavaDamage && source.isIn(net.minecraft.registry.tag.DamageTypeTags.IS_FIRE) && amount >= 2F) {
            discard();
        }
        return super.damage(source, amount);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (!getWorld().isClient && player instanceof ServerPlayerEntity playerMP) {
            if (CorpseConfig.onlyOwnerAccess) {
                boolean isOp = playerMP.hasPermissionLevel(playerMP.getServer().getOpPermissionLevel());
                Optional<UUID> corpseUuid = getCorpseUUID();
                if (isOp || !corpseUuid.isPresent() || playerMP.getUuid().equals(corpseUuid.get())) {
                    CorpseGuis.openCorpseGUI(playerMP, this);
                } else if (CorpseConfig.skeletonAccess && isSkeleton()) {
                    CorpseGuis.openCorpseGUI(playerMP, this);
                }
            } else {
                CorpseGuis.openCorpseGUI(playerMP, this);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public Text getDisplayName() {
        String name = getCorpseName();
        if (name == null || name.trim().isEmpty()) {
            return super.getDisplayName();
        } else {
            return Text.translatable("entity.ancestralpowers.corpse_of", name);
        }
    }

    @Override
    public boolean doesRenderOnFire() {
        return false;
    }

    @Override
    public boolean isCollidable() {
        return isAlive();
    }

    @Override
    public boolean canHit() {
        return isAlive();
    }

    // Synced data accessors
    public Optional<UUID> getCorpseUUID() {
        return dataTracker.get(CORPSE_UUID);
    }

    public void setCorpseUUID(UUID uuid) {
        dataTracker.set(CORPSE_UUID, Optional.ofNullable(uuid));
    }

    public String getCorpseName() {
        return dataTracker.get(CORPSE_NAME);
    }

    public void setCorpseName(String name) {
        dataTracker.set(CORPSE_NAME, name);
    }

    public boolean isSkeleton() {
        return dataTracker.get(IS_SKELETON);
    }

    public void setSkeleton(boolean skeleton) {
        dataTracker.set(IS_SKELETON, skeleton);
    }

    public byte getCorpseModel() {
        return dataTracker.get(CORPSE_MODEL);
    }

    public void setCorpseModel(byte model) {
        dataTracker.set(CORPSE_MODEL, model);
    }

    // Skin data getters (synced via DataTracker)
    public String getSkinValue() {
        return dataTracker.get(SKIN_VALUE);
    }

    public String getSkinSignature() {
        return dataTracker.get(SKIN_SIGNATURE);
    }

    public Death getDeath() {
        return death;
    }

    public DefaultedList<ItemStack> getEquipment() {
        String json = dataTracker.get(EQUIPMENT_JSON);
        DefaultedList<ItemStack> equipment = DefaultedList.ofSize(EquipmentSlot.values().length, ItemStack.EMPTY);
        if (json != null && !json.isEmpty()) {
            try {
                NbtElement element = net.minecraft.nbt.StringNbtReader.parse(json);
                if (element instanceof NbtList list) {
                    for (int i = 0; i < list.size(); i++) {
                        NbtCompound itemNbt = list.getCompound(i);
                        int slot = itemNbt.getByte("Slot") & 255;
                        if (slot < EquipmentSlot.values().length) {
                            equipment.set(slot, ItemStack.fromNbt(itemNbt));
                        }
                    }
                }
            } catch (Exception e) {
                // Ignore parse errors
            }
        }
        return equipment;
    }

    private void setEquipmentFromJson(String json) {
        DefaultedList<ItemStack> equipment = DefaultedList.ofSize(EquipmentSlot.values().length, ItemStack.EMPTY);
        if (json != null && !json.isEmpty()) {
            try {
                NbtElement element = net.minecraft.nbt.StringNbtReader.parse(json);
                if (element instanceof NbtList list) {
                    for (int i = 0; i < list.size(); i++) {
                        NbtCompound itemNbt = list.getCompound(i);
                        int slot = itemNbt.getByte("Slot") & 255;
                        if (slot < EquipmentSlot.values().length) {
                            equipment.set(slot, ItemStack.fromNbt(itemNbt));
                        }
                    }
                }
            } catch (Exception e) {
                // Ignore parse errors
            }
        }
        this.death.setEquipment(equipment);
        this.dataTracker.set(EQUIPMENT_JSON, json);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound compound) {
        if (compound.contains("Death")) {
            death = Death.fromNbt(compound.getCompound("Death"));
        } else {
            UUID playerUUID = new UUID(compound.getLong("IDMost"), compound.getLong("IDLeast"));
            UUID deathID = new UUID(compound.getLong("DeathIDMost"), compound.getLong("DeathIDLeast"));
            Death.Builder builder = new Death.Builder(playerUUID, deathID);
            int size = compound.getInt("InventorySize");
            DefaultedList<ItemStack> additionalItems = DefaultedList.ofSize(size, ItemStack.EMPTY);
            readItemList(compound, "Inventory", additionalItems);
            builder.additionalItems(additionalItems);
            DefaultedList<ItemStack> equipment = DefaultedList.ofSize(EquipmentSlot.values().length, ItemStack.EMPTY);
            readEquipment(compound, "Equipment", equipment);
            builder.equipment(equipment);
            builder.playerName(compound.getString("Name"));
            death = builder.build();
        }
        if (compound.contains("EquipmentJson")) {
            dataTracker.set(EQUIPMENT_JSON, compound.getString("EquipmentJson"));
        } else if (compound.contains("Equipment")) {
            dataTracker.set(EQUIPMENT_JSON, compound.getList("Equipment", 10).toString());
        }
        if (compound.contains("SkinValue")) {
            dataTracker.set(SKIN_VALUE, compound.getString("SkinValue"));
        }
        if (compound.contains("SkinSignature")) {
            dataTracker.set(SKIN_SIGNATURE, compound.getString("SkinSignature"));
        }
        setEquipmentFromJson(dataTracker.get(EQUIPMENT_JSON));
        setCorpseUUID(death.getPlayerUUID());
        setCorpseName(death.getPlayerName());
        setCorpseModel(death.getModel());
        age = compound.getInt("Age");
        if (compound.contains("EmptyAge")) {
            emptyAge = compound.getInt("EmptyAge");
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound compound) {
        compound.put("Death", death.toNbt());
        compound.putInt("Age", age);
        if (emptyAge >= 0) {
            compound.putInt("EmptyAge", emptyAge);
        }

        DefaultedList<ItemStack> equipment = getEquipment();
        NbtList equipmentList = new NbtList();
        for (int i = 0; i < equipment.size(); i++) {
            ItemStack stack = equipment.get(i);
            if (!stack.isEmpty()) {
                NbtCompound itemNbt = new NbtCompound();
                itemNbt.putByte("Slot", (byte) i);
                stack.writeNbt(itemNbt);
                equipmentList.add(itemNbt);
            }
        }
        compound.put("Equipment", equipmentList);
        compound.putString("EquipmentJson", dataTracker.get(EQUIPMENT_JSON));
        String skinVal = dataTracker.get(SKIN_VALUE);
        String skinSig = dataTracker.get(SKIN_SIGNATURE);
        if (!skinVal.isEmpty()) compound.putString("SkinValue", skinVal);
        if (!skinSig.isEmpty()) compound.putString("SkinSignature", skinSig);
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    private void readItemList(NbtCompound compound, String key, DefaultedList<ItemStack> items) {
        NbtList list = compound.getList(key, 10);
        for (int i = 0; i < list.size(); i++) {
            NbtCompound itemNbt = list.getCompound(i);
            int slot = itemNbt.getByte("Slot") & 255;
            if (slot < items.size()) {
                items.set(slot, ItemStack.fromNbt(itemNbt));
            }
        }
    }

    private void readEquipment(NbtCompound compound, String key, DefaultedList<ItemStack> equipment) {
        NbtList list = compound.getList(key, 10);
        for (int i = 0; i < list.size(); i++) {
            NbtCompound itemNbt = list.getCompound(i);
            int slot = itemNbt.getByte("Slot") & 255;
            if (slot < equipment.size()) {
                equipment.set(slot, ItemStack.fromNbt(itemNbt));
            }
        }
    }

}
