package dev.joaq.ancestralpowers.powers.secondary;

import dev.joaq.ancestralpowers.components.MyComponents;
import dev.joaq.ancestralpowers.components.PlayerTraits;
import dev.joaq.ancestralpowers.powers.PowerBase;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class SpeedPower extends PowerBase {

    private static final UUID MOVEMENT_SPEED_ID = UUID.nameUUIDFromBytes("ancestralpowers:super_speed".getBytes());
    private static final UUID ATTACK_SPEED_ID = UUID.nameUUIDFromBytes("ancestralpowers:super_attack_speed".getBytes());

    @Override
    protected float staminaCost() {
        return 0.5f;
    }

    @Override
    protected String ActivationType() {
        return "TOGGLE";
    }

    @Override
     protected void disablePowerSpecific(ServerPlayerEntity player) {
         EntityAttributeInstance movementSpeedAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
         EntityAttributeInstance attackSpeedAttr = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED);

         removeModifier(movementSpeedAttr, MOVEMENT_SPEED_ID);
         removeModifier(attackSpeedAttr, ATTACK_SPEED_ID);

         PlayerTraits traits = MyComponents.TRAITS.get(player);
         traits.setActPower_secondary(false);
     }

     @Override
     protected void executeLogic(ServerPlayerEntity player, boolean activate, float stamina) {
         EntityAttributeInstance movementSpeedAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
         EntityAttributeInstance attackSpeedAttr = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED);

         if (movementSpeedAttr == null ||attackSpeedAttr == null) return;


         removeModifier(movementSpeedAttr, MOVEMENT_SPEED_ID);
         removeModifier(attackSpeedAttr, ATTACK_SPEED_ID);

         addModifier(movementSpeedAttr, MOVEMENT_SPEED_ID, 0.1);
         addModifier(attackSpeedAttr, ATTACK_SPEED_ID, 1.0);
     }

    private void removeModifier(EntityAttributeInstance attr, UUID id) {
         if (attr == null) return;
         attr.removeModifier(id);
     }

     private void addModifier(EntityAttributeInstance attr, UUID id, double value) {
         attr.addPersistentModifier(new EntityAttributeModifier(id, "ancestralpowers_speed", value, EntityAttributeModifier.Operation.ADDITION));
     }

    @Override
    public void apply(ServerPlayerEntity player, boolean activate, float stamina) {
        PlayerTraits traits = MyComponents.TRAITS.get(player);
        execute(player, activate, ActivationType(), traits, "Specific");
    }

    @Override
    public void reset(ServerPlayerEntity player) {

         EntityAttributeInstance movementSpeedAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
         EntityAttributeInstance attackSpeedAttr = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED);

         removeModifier(movementSpeedAttr, MOVEMENT_SPEED_ID);
         removeModifier(attackSpeedAttr, ATTACK_SPEED_ID);
    }
}
