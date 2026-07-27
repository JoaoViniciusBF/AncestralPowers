package dev.joaq.ancestralpowers.powers.secondary;

import dev.joaq.ancestralpowers.components.MyComponents;
import dev.joaq.ancestralpowers.components.PlayerTraits;
import dev.joaq.ancestralpowers.powers.PowerBase;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class DecreaseScalePower extends PowerBase {

    private static final UUID MOVEMENT_SPEED_ID = UUID.nameUUIDFromBytes("ancestralpowers:move_speed".getBytes());

    private static final double INCREMENT = -0.25;
    private static final double MIN_SCALE = 0.5;
    private static final double MAX_SCALE = 16.0;
    private static final double EPS = 0.001;

    private void removeModifier(EntityAttributeInstance attr, UUID id) {
         if (attr == null) return;
         attr.removeModifier(id);
     }

    @Override
    protected float staminaCost() {
        return 0.25f;
    }

    @Override
    protected String ActivationType() {
        return "PRESS-PERSISTENT";
    }

    @Override
    protected void disablePowerSpecific(ServerPlayerEntity player) {
        EntityAttributeInstance speedAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);

        removeModifier(speedAttr, MOVEMENT_SPEED_ID);

        PlayerTraits traits = MyComponents.TRAITS.get(player);
        traits.setScaleMultiplier(1.0);
        traits.setActPower_main(false);
        traits.setActPower_secondary(false);
    }

    @Override
    protected void executeLogic(ServerPlayerEntity player, boolean activate, float stamina) {
        EntityAttributeInstance speedAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (speedAttr == null) return;

        PlayerTraits traits = MyComponents.TRAITS.get(player);
        double currentScale = traits.getScaleMultiplier();

        double next = currentScale + INCREMENT;
        next = Math.max(MIN_SCALE, Math.min(MAX_SCALE, next));

        if (Math.abs(next - 1.0) < EPS) {
            removeModifier(speedAttr, MOVEMENT_SPEED_ID);
            traits.setScaleMultiplier(1.0);
            return;
        }

        speedAttr.removeModifier(MOVEMENT_SPEED_ID);
        speedAttr.addPersistentModifier(new EntityAttributeModifier(
                MOVEMENT_SPEED_ID, "ancestralpowers_move_speed", next / 10.0 - 0.1, EntityAttributeModifier.Operation.ADDITION
        ));

        traits.setScaleMultiplier(next);
    }

    protected boolean customIsActive(ServerPlayerEntity player) {
        PlayerTraits traits = MyComponents.TRAITS.get(player);
        return Math.abs(traits.getScaleMultiplier() - 1.0) > EPS;
    }

    @Override
    public void apply(ServerPlayerEntity player, boolean activate, float stamina) {
        PlayerTraits traits = MyComponents.TRAITS.get(player);
        execute(player, activate, ActivationType(), traits, "Specific", customIsActive(player));
    }
    @Override
    public void reset(ServerPlayerEntity player) {
        disablePowerSpecific(player);
    }

}
