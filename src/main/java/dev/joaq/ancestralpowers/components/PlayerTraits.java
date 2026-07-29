package dev.joaq.ancestralpowers.components;


import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import dev.onyxstudios.cca.api.v3.component.Component;

import java.util.UUID;

public interface PlayerTraits extends Component {

    UUID getArenaTarget();
    void setArenaTarget(UUID arenaTarget);

    Double getScaleMultiplier();
    void setScaleMultiplier(Double scaleMultiplier);

    Vec3d getUsagePosition();
    void clearUsagePosition();
    void setUsagePosition(Vec3d pos);

    Vec3d getTeleportTarget();
    void clearTeleportTarget();
    void setTeleportTarget(Vec3d pos);

    Float getStamina();
    void setStamina(Float stamina);

    Integer getPersonalDimensionValue();
    void setPersonalDimensionValue(Integer value);

    Boolean getPersonalDimensionGenerated();
    void setPersonalDimensionGenerated(Boolean Generated);

    Boolean getInArena();
    void setInArena(Boolean inArena);

    Integer getArenaValue();
    void setArenaValue(Integer value2);

    Boolean getArenaGenerated();
    void setArenaGenerated(Boolean Generated2);

    Boolean getActPower_main();
    void setActPower_main(Boolean actPower_main);

    Boolean getActPower_secondary();
    void setActPower_secondary(Boolean actPower_secondary);

    String getMovementPower();
    void setMovementPower(String power);

    String getMainPower();
    void setMainPower(String power);

    String getIntelligence();
    void setIntelligence(String intelligence);

    CloneData getCloneData();
    void setCloneData(CloneData cloneData);

    Boolean getInSpectralForm();
    void setInSpectralForm(Boolean inSpectralForm);

    Vec3d getSpectralBodyPosition();
    void setSpectralBodyPosition(Vec3d pos);
    void clearSpectralBodyPosition();

    UUID getSpectralBodyCloneId();
    void setSpectralBodyCloneId(UUID cloneId);
    void clearSpectralBodyCloneId();

    NbtCompound getSpectralBodyInventory();
    void setSpectralBodyInventory(NbtCompound inventory);
void clearSpectralBodyInventory();

    Boolean getIsDowned();
    void setIsDowned(Boolean isDowned);

    Integer getBleedoutTimer();
    void setBleedoutTimer(Integer bleedoutTimer);

    Integer getReviveProgress();
    void setReviveProgress(Integer reviveProgress);

    UUID getReviverUuid();
    void setReviverUuid(UUID reviverUuid);
    void clearReviverUuid();

}


