package dev.joaq.ancestralpowers.components;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import dev.onyxstudios.cca.api.v3.component.Component;

import java.util.UUID;

public class PlayerTraitsComponent implements PlayerTraits {

    private double scaleMultiplier = 1;

    private Vec3d teleportTarget;
    private Vec3d usagePosition;

    private boolean inArena;
    private boolean Generated;
    private boolean Generated2;
    private boolean actPower_main;
    private boolean actPower_secondary;

    private Float stamina = 100f;

    private int value;
    private int value2;

    private UUID arenaTarget;

    private String movement;
    private String main;
    private String intelligence;

    private CloneData cloneData = new CloneData();

    private boolean inSpectralForm = false;
    private Vec3d spectralBodyPosition;
    private UUID spectralBodyCloneId;
    private NbtCompound spectralBodyInventory;

    private boolean isDowned = false;
    private int bleedoutTimer = 0;
    private UUID reviverUuid;
    private int reviveProgress = 0;

    @Override
    public UUID getArenaTarget() {
        return this.arenaTarget;
    }

    @Override
    public void setArenaTarget(UUID arenaTarget) {
        this.arenaTarget = arenaTarget;
    }

    @Override
    public Double getScaleMultiplier() {
        return this.scaleMultiplier;
    }

    @Override
    public void setScaleMultiplier(Double scaleMultiplier) {
        this.scaleMultiplier = scaleMultiplier;
    }

    @Override
    public Vec3d getTeleportTarget() {
        return this.teleportTarget;
    }

    @Override
    public void setTeleportTarget(Vec3d pos) {
        this.teleportTarget = pos;
    }

    public void clearTeleportTarget() {
        this.teleportTarget = null;
    }

    @Override
    public Vec3d getUsagePosition() {
        return this.usagePosition;
    }

    @Override
    public void setUsagePosition(Vec3d pos) {
        this.usagePosition = pos;
    }

    public void clearUsagePosition() {
        this.usagePosition = null;
    }

    @Override
    public Float getStamina() {
        return stamina;
    }

    @Override
    public void setStamina(Float stamina) {
        this.stamina = stamina;
    }

    @Override
    public Integer getPersonalDimensionValue() {
        return value;
    }

    @Override
    public void setPersonalDimensionValue(Integer value) {
        this.value = value;
    }

    @Override
    public Boolean getPersonalDimensionGenerated() {
        return Generated;
    }

    @Override
    public void setPersonalDimensionGenerated(Boolean Generated) {
        this.Generated = Generated;
    }

    @Override
    public Boolean getInArena() {
        return inArena;
    }

    @Override
    public void setInArena(Boolean inArena) {
        this.inArena = inArena;
    }

    @Override
    public Integer getArenaValue() {
        return value2;
    }

    @Override
    public void setArenaValue(Integer value2) {
        this.value2 = value2;
    }

    @Override
    public Boolean getArenaGenerated() {
        return Generated2;
    }

    @Override
    public void setArenaGenerated(Boolean Generated2) {
        this.Generated2 = Generated2;
    }

    @Override
    public Boolean getActPower_main() {
        return actPower_main;

    }

    @Override
    public void setActPower_main(Boolean actPower_main) {
        this.actPower_main = actPower_main;

    }

    @Override
    public Boolean getActPower_secondary() {
        return actPower_secondary;
    }

    @Override
    public void setActPower_secondary(Boolean actPower_secondary) {
        this.actPower_secondary = actPower_secondary;

    }

    @Override
    public String getMovementPower() {
        return movement;
    }

    @Override
    public void setMovementPower(String power) {
        this.movement = power;
    }

    @Override
    public String getMainPower() {
        return main;
    }

    @Override
    public void setMainPower(String power) {
        this.main = power;
    }

    @Override
    public String getIntelligence() {
        return intelligence;
    }

    @Override
    public void setIntelligence(String intelligence) {
        this.intelligence = intelligence;
    }

    @Override
    public CloneData getCloneData() {
        return this.cloneData;
    }

    @Override
    public void setCloneData(CloneData cloneData) {
        this.cloneData = cloneData;
    }

    @Override
    public Boolean getInSpectralForm() {
        return this.inSpectralForm;
    }

    @Override
    public void setInSpectralForm(Boolean inSpectralForm) {
        this.inSpectralForm = inSpectralForm;
    }

    @Override
    public Vec3d getSpectralBodyPosition() {
        return this.spectralBodyPosition;
    }

    @Override
    public void setSpectralBodyPosition(Vec3d pos) {
        this.spectralBodyPosition = pos;
    }

    @Override
    public void clearSpectralBodyPosition() {
        this.spectralBodyPosition = null;
    }

    @Override
    public UUID getSpectralBodyCloneId() {
        return this.spectralBodyCloneId;
    }

    @Override
    public void setSpectralBodyCloneId(UUID cloneId) {
        this.spectralBodyCloneId = cloneId;
    }

    @Override
    public void clearSpectralBodyCloneId() {
        this.spectralBodyCloneId = null;
    }

    @Override
    public NbtCompound getSpectralBodyInventory() {
        return this.spectralBodyInventory;
    }

    @Override
    public void setSpectralBodyInventory(NbtCompound inventory) {
        this.spectralBodyInventory = inventory;
    }

    @Override
    public void clearSpectralBodyInventory() {
        this.spectralBodyInventory = null;
    }

    @Override
    public Boolean getIsDowned() {
        return this.isDowned;
    }

    @Override
    public void setIsDowned(Boolean isDowned) {
        this.isDowned = isDowned;
    }

    @Override
    public Integer getBleedoutTimer() {
        return this.bleedoutTimer;
    }

    @Override
    public void setBleedoutTimer(Integer bleedoutTimer) {
        this.bleedoutTimer = bleedoutTimer;
    }

    @Override
    public UUID getReviverUuid() {
        return this.reviverUuid;
    }

    @Override
    public void setReviverUuid(UUID reviverUuid) {
        this.reviverUuid = reviverUuid;
    }

    @Override
    public void clearReviverUuid() {
        this.reviverUuid = null;
    }

    @Override
    public Integer getReviveProgress() {
        return this.reviveProgress;
    }

    @Override
    public void setReviveProgress(Integer reviveProgress) {
        this.reviveProgress = reviveProgress;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.movement = tag.getString("movement");
        this.main = tag.getString("main");
        this.intelligence = tag.getString("intelligence");
        this.Generated = tag.getBoolean("Generated");
        this.value = tag.getInt("value");

        this.Generated2 = tag.getBoolean("Generated2");
        this.value2 = tag.getInt("value2");

        this.actPower_main = tag.getBoolean("actPower_main");
        this.actPower_secondary = tag.getBoolean("actPower_secondary");
        
        if (tag.contains("scaleMultiplier")) {
            this.scaleMultiplier = tag.getDouble("scaleMultiplier");
        } else {
            this.scaleMultiplier = 1.0;
        }
        
        if (tag.contains("stamina")) {
            this.stamina = tag.getFloat("stamina");
        } else if (this.stamina == null) {
            this.stamina = 100f;
        }
        
        if (tag.contains("teleportPosX")) {
            double x = tag.getDouble("teleportPosX");
            double y = tag.getDouble("teleportPosY");
            double z = tag.getDouble("teleportPosZ");
            this.teleportTarget = new Vec3d(x, y, z);
        } else {
            this.teleportTarget = null;
        }
        
        if (tag.contains("usagePosX")) {
            double x = tag.getDouble("usagePosX");
            double y = tag.getDouble("usagePosY");
            double z = tag.getDouble("usagePosZ");
            this.usagePosition = new Vec3d(x, y, z);
        } else {
            this.usagePosition = null;
        }
        if (tag.contains("arenaTarget")) {
            String uuidStr = tag.getString("arenaTarget");
            if (!uuidStr.isEmpty()) {
                this.arenaTarget = UUID.fromString(uuidStr);
            } else {
                this.arenaTarget = null;
            }
        } else {
            this.arenaTarget = null;
        }

        if (tag.contains("cloneData")) {
            this.cloneData = CloneData.fromNbt(tag.getCompound("cloneData"));
        } else {
            this.cloneData = new CloneData();
        }

        this.inSpectralForm = tag.getBoolean("inSpectralForm");

        if (tag.contains("spectralBodyPosX")) {
            double x = tag.getDouble("spectralBodyPosX");
            double y = tag.getDouble("spectralBodyPosY");
            double z = tag.getDouble("spectralBodyPosZ");
            this.spectralBodyPosition = new Vec3d(x, y, z);
        } else {
            this.spectralBodyPosition = null;
        }

        if (tag.contains("spectralBodyCloneId")) {
            String uuidStr = tag.getString("spectralBodyCloneId");
            if (!uuidStr.isEmpty()) {
                this.spectralBodyCloneId = UUID.fromString(uuidStr);
            } else {
                this.spectralBodyCloneId = null;
            }
        } else {
            this.spectralBodyCloneId = null;
        }

        if (tag.contains("spectralBodyInventory")) {
            this.spectralBodyInventory = tag.getCompound("spectralBodyInventory");
        } else {
            this.spectralBodyInventory = null;
        }

        this.isDowned = tag.getBoolean("isDowned");
        this.bleedoutTimer = tag.getInt("bleedoutTimer");
        
        if (tag.contains("reviverUuid")) {
            String uuidStr = tag.getString("reviverUuid");
            if (!uuidStr.isEmpty()) {
                this.reviverUuid = UUID.fromString(uuidStr);
            } else {
                this.reviverUuid = null;
            }
        } else {
            this.reviverUuid = null;
        }
        
        this.reviveProgress = tag.getInt("reviveProgress");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putString("movement", movement != null ? movement : "");
        tag.putString("main", main != null ? main : "");
        tag.putString("intelligence", intelligence != null ? intelligence : "");

        tag.putBoolean("Generated", Generated);
        tag.putBoolean("Generated2", Generated2);

        tag.putBoolean("actPower_main", actPower_main);
        tag.putBoolean("actPower_secondary", actPower_secondary);

        tag.putDouble("scaleMultiplier", scaleMultiplier);
        tag.putFloat("stamina", stamina);

        tag.putInt("value", value);
        tag.putInt("value2", value2);

        if (teleportTarget != null) {
            tag.putDouble("teleportPosX", teleportTarget.x);
            tag.putDouble("teleportPosY", teleportTarget.y);
            tag.putDouble("teleportPosZ", teleportTarget.z);
        }
        
        if (usagePosition != null) {
            tag.putDouble("usagePosX", usagePosition.x);
            tag.putDouble("usagePosY", usagePosition.y);
            tag.putDouble("usagePosZ", usagePosition.z);
        }
        if (arenaTarget != null) {
            tag.putString("arenaTarget", arenaTarget.toString());
        }

        if (cloneData != null) {
            tag.put("cloneData", cloneData.toNbt());
        }

        tag.putBoolean("inSpectralForm", inSpectralForm);

        if (spectralBodyPosition != null) {
            tag.putDouble("spectralBodyPosX", spectralBodyPosition.x);
            tag.putDouble("spectralBodyPosY", spectralBodyPosition.y);
            tag.putDouble("spectralBodyPosZ", spectralBodyPosition.z);
        }

        if (spectralBodyCloneId != null) {
            tag.putString("spectralBodyCloneId", spectralBodyCloneId.toString());
        }

        if (spectralBodyInventory != null) {
            tag.put("spectralBodyInventory", spectralBodyInventory);
        }

        tag.putBoolean("isDowned", isDowned);
        tag.putInt("bleedoutTimer", bleedoutTimer);
        
        if (reviverUuid != null) {
            tag.putString("reviverUuid", reviverUuid.toString());
        }
        tag.putInt("reviveProgress", reviveProgress);
    }

}