package dev.joaq.ancestralpowers.components;


import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.Vec3d;
import org.ladysnake.cca.api.v3.component.Component;

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

}

    class PlayerTraitsComponent implements PlayerTraits {

        private double scaleMultiplier = 1;

        private Vec3d teleportTarget;
        private Vec3d usagePosition;

        private boolean inArena;
        private boolean Generated;
        private boolean Generated2;
        private boolean actPower_main;
        private boolean actPower_secondary;

        private Float stamina;

        private int value;
        private int value2;

        private UUID arenaTarget;

        private String movement;
        private String main;
        private String intelligence;

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
        public void writeData(WriteView writeView) {
            writeView.putString("movement", movement != null ? movement : "");
            writeView.putString("main", main != null ? main : "");
            writeView.putString("intelligence", intelligence != null ? intelligence : "");

            writeView.putBoolean("Generated",Generated);
            writeView.putBoolean("Generated2",Generated2);

            writeView.putBoolean("actPower_main",actPower_main);
            writeView.putBoolean("actPower_secondary",actPower_secondary);

            writeView.putFloat("stamina",stamina);

            writeView.putInt("value",value);
            writeView.putInt("value2",value2);

            if (usagePosition != null) {
                writeView.putDouble("usagePosX", usagePosition.x);
                writeView.putDouble("usagePosY", usagePosition.y);
                writeView.putDouble("usagePosZ", usagePosition.z);
            }
            if (arenaTarget != null) {
                writeView.putString("arenaTarget",arenaTarget.toString());
            }
        }

        @Override
        public void readData(ReadView readView) {
            this.movement = readView.getString("movement", movement);
            this.main = readView.getString("main", main);
            this.intelligence = readView.getString("intelligence", intelligence);
            this.Generated = readView.getBoolean("Generated", Generated);
            this.value = readView.getInt("value", value);

            this.Generated2 = readView.getBoolean("Generated2", Generated2);
            this.value2 = readView.getInt("value2", value2);

            this.actPower_main = readView.getBoolean("actPower_main", actPower_main);
            this.actPower_secondary = readView.getBoolean("actPower_secondary", actPower_secondary);
            if (readView.contains("stamina")) {
                this.stamina = readView.getFloat("stamina", 100f);
            } else if (this.stamina == null) {
                this.stamina = 100f;
            }
            if (readView.contains("usagePosX")) {
                double x = readView.getDouble("usagePosX", 0);
                double y = readView.getDouble("usagePosY", 0);
                double z = readView.getDouble("usagePosZ", 0);
                this.usagePosition = new Vec3d(x, y, z);
            } else {
                this.usagePosition = null;
            }
            if (readView.contains("arenaTarget")) {
                String uuidStr = readView.getString("arenaTarget", "");
                if (!uuidStr.isEmpty()) {
                    this.arenaTarget = UUID.fromString(uuidStr);
                } else {
                    this.arenaTarget = null;
                }
            } else {
                this.arenaTarget = null;
            }
        }

        }


