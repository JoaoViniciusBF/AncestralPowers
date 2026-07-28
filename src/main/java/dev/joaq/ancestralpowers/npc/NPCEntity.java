package dev.joaq.ancestralpowers.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

public class NPCEntity extends LivingEntity {
    private static final TrackedData<String> NPC_TYPE = DataTracker.registerData(NPCEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<String> OWNER_NAME = DataTracker.registerData(NPCEntity.class, TrackedDataHandlerRegistry.STRING);
    
    private UUID npcDataId;
    private UUID ownerUuid;
    private GameProfile gameProfile;
    private DefaultedList<ItemStack> inventory;
    private DefaultedList<ItemStack> armorItems;
    
    public NPCEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
        this.inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
        this.armorItems = DefaultedList.ofSize(4, ItemStack.EMPTY);
    }

    public static DefaultAttributeContainer createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_ARMOR, 0.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.0)
                .build();
    }
    
    public NPCEntity(EntityType<? extends LivingEntity> entityType, World world, NPCData data) {
        this(entityType, world);
        this.npcDataId = data.getNpcId();
        this.ownerUuid = data.getOwnerUuid();
        
        if (data.getPosition() != null) {
            this.setPosition(Vec3d.ofCenter(data.getPosition()));
        }
        this.setYaw(data.getYaw());
        this.setPitch(data.getPitch());
        this.bodyYaw = data.getYaw();
        this.headYaw = data.getYaw();
        this.prevYaw = data.getYaw();
        this.prevBodyYaw = data.getYaw();
        this.prevHeadYaw = data.getYaw();
        
        this.setHealth(data.getHealth());
        
        if (data.getOwnerName() != null) {
            this.gameProfile = new GameProfile(data.getOwnerUuid(), data.getOwnerName());
            
            if (data.getSkinValue() != null && data.getSkinSignature() != null) {
                this.gameProfile.getProperties().put("textures", 
                    new Property("textures", data.getSkinValue(), data.getSkinSignature()));
            }
        }
        
        for (int i = 0; i < Math.min(4, data.getArmor().size()); i++) {
            this.armorItems.set(i, data.getArmor().get(i).copy());
        }
        
        if (data.getInventory().size() > 0 && !data.getInventory().get(0).isEmpty()) {
            this.inventory.set(0, data.getInventory().get(0).copy());
        }
        if (!data.getOffhand().isEmpty()) {
            this.inventory.set(1, data.getOffhand().copy());
        }
        
        this.dataTracker.set(NPC_TYPE, data.getType().getId());
        this.dataTracker.set(OWNER_NAME, data.getOwnerName() != null ? data.getOwnerName() : "");
        
        data.getEffects().forEach(this::addStatusEffect);
        
        this.setCustomName(Text.literal("§6" + data.getType().getId().toUpperCase() + " §7(" + data.getOwnerName() + ")"));
        this.setCustomNameVisible(true);
    }
    
    @Override
    public boolean damage(DamageSource source, float amount) {
        boolean result = super.damage(source, amount);
        if (result && !this.getWorld().isClient && this.npcDataId != null && this.ownerUuid != null) {
            NPCManager.updateHealth(this.npcDataId, this.ownerUuid, (ServerWorld) this.getWorld(), this.getHealth());
        }
        return result;
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        if (!this.getWorld().isClient && this.npcDataId != null && this.ownerUuid != null) {
            NPCManager.removeNPC((ServerWorld) this.getWorld(), this.ownerUuid, this.npcDataId);
        }
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(NPC_TYPE, "generic");
        this.dataTracker.startTracking(OWNER_NAME, "");
    }
    
    @Override
    public Iterable<ItemStack> getArmorItems() {
        return this.armorItems;
    }
    
    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        switch (slot.getType()) {
            case HAND:
                return this.inventory.get(slot.getEntitySlotId());
            case ARMOR:
                return this.armorItems.get(slot.getEntitySlotId());
            default:
                return ItemStack.EMPTY;
        }
    }
    
    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {
        switch (slot.getType()) {
            case HAND:
                this.inventory.set(slot.getEntitySlotId(), stack);
                break;
            case ARMOR:
                this.armorItems.set(slot.getEntitySlotId(), stack);
                break;
        }
    }
    
    @Override
    public Arm getMainArm() {
        return Arm.RIGHT;
    }
    
    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (!this.getWorld().isClient && player instanceof ServerPlayerEntity serverPlayer) {
            if (this.ownerUuid != null && this.ownerUuid.equals(player.getUuid())) {
                player.sendMessage(Text.literal("§aEste é seu NPC! ID: " + this.npcDataId), false);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }
    
    @Override
    public void tick() {
        super.tick();
    }
    
    @Override
    public boolean isPushable() {
        return true;
    }
    
    @Override
    public boolean isCollidable() {
        return true;
    }
    
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        
        if (nbt.containsUuid("npcDataId")) {
            this.npcDataId = nbt.getUuid("npcDataId");
        }
        if (nbt.containsUuid("ownerUuid")) {
            this.ownerUuid = nbt.getUuid("ownerUuid");
        }
        if (nbt.contains("ownerName")) {
            String ownerName = nbt.getString("ownerName");
            this.dataTracker.set(OWNER_NAME, ownerName);
            if (this.ownerUuid != null) {
                this.gameProfile = new GameProfile(this.ownerUuid, ownerName);
            }
        }
        if (nbt.contains("npcType")) {
            this.dataTracker.set(NPC_TYPE, nbt.getString("npcType"));
        }
        
        if (nbt.contains("skinValue") && nbt.contains("skinSignature")) {
            String skinValue = nbt.getString("skinValue");
            String skinSignature = nbt.getString("skinSignature");
            if (this.gameProfile != null) {
                this.gameProfile.getProperties().put("textures",
                    new Property("textures", skinValue, skinSignature));
            }
        }
        
        if (nbt.contains("Inventory")) {
            var inventoryList = nbt.getList("Inventory", 10);
            for (int i = 0; i < inventoryList.size(); i++) {
                NbtCompound itemNbt = inventoryList.getCompound(i);
                int slot = itemNbt.getByte("Slot") & 255;
                if (slot < this.inventory.size()) {
                    this.inventory.set(slot, ItemStack.fromNbt(itemNbt));
                }
            }
        }
        
        if (nbt.contains("Armor")) {
            var armorList = nbt.getList("Armor", 10);
            for (int i = 0; i < armorList.size(); i++) {
                NbtCompound itemNbt = armorList.getCompound(i);
                int slot = itemNbt.getByte("Slot") & 255;
                if (slot < this.armorItems.size()) {
                    this.armorItems.set(slot, ItemStack.fromNbt(itemNbt));
                }
            }
        }
    }
    
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        
        if (this.npcDataId != null) {
            nbt.putUuid("npcDataId", this.npcDataId);
        }
        if (this.ownerUuid != null) {
            nbt.putUuid("ownerUuid", this.ownerUuid);
        }
        
        nbt.putString("ownerName", this.dataTracker.get(OWNER_NAME));
        nbt.putString("npcType", this.dataTracker.get(NPC_TYPE));
        
        if (this.gameProfile != null && this.gameProfile.getProperties().containsKey("textures")) {
            Property textures = this.gameProfile.getProperties().get("textures").iterator().next();
            nbt.putString("skinValue", textures.value());
            nbt.putString("skinSignature", textures.signature());
        }
        
        var inventoryList = new net.minecraft.nbt.NbtList();
        for (int i = 0; i < this.inventory.size(); i++) {
            if (!this.inventory.get(i).isEmpty()) {
                NbtCompound itemNbt = new NbtCompound();
                itemNbt.putByte("Slot", (byte) i);
                this.inventory.get(i).writeNbt(itemNbt);
                inventoryList.add(itemNbt);
            }
        }
        nbt.put("Inventory", inventoryList);
        
        var armorList = new net.minecraft.nbt.NbtList();
        for (int i = 0; i < this.armorItems.size(); i++) {
            if (!this.armorItems.get(i).isEmpty()) {
                NbtCompound itemNbt = new NbtCompound();
                itemNbt.putByte("Slot", (byte) i);
                this.armorItems.get(i).writeNbt(itemNbt);
                armorList.add(itemNbt);
            }
        }
        nbt.put("Armor", armorList);
    }
    
    public UUID getNpcDataId() {
        return npcDataId;
    }
    
    public void setNpcDataId(UUID npcDataId) {
        this.npcDataId = npcDataId;
    }
    
    public UUID getOwnerUuid() {
        return ownerUuid;
    }
    
    public void setOwnerUuid(UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
    }
    
    public GameProfile getGameProfile() {
        return gameProfile;
    }
    
    public void setGameProfile(GameProfile gameProfile) {
        this.gameProfile = gameProfile;
    }
    
    public String getNpcType() {
        return this.dataTracker.get(NPC_TYPE);
    }
    
    public String getOwnerName() {
        return this.dataTracker.get(OWNER_NAME);
    }
}