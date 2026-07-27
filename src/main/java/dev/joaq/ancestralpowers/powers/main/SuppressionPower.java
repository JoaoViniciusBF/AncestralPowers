package dev.joaq.ancestralpowers.powers.main;

import dev.joaq.ancestralpowers.components.MyComponents;
import dev.joaq.ancestralpowers.components.PlayerTraits;
import dev.joaq.ancestralpowers.powers.PowerBase;
import dev.joaq.ancestralpowers.registry.ModEffects;
import dev.joaq.ancestralpowers.util.PlayerUtils;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;

public class SuppressionPower extends PowerBase {

    @Override
    public void apply(ServerPlayerEntity player, boolean activate, float stamina) {
        PlayerTraits traits = MyComponents.TRAITS.get(player);
        execute(player, activate, ActivationType(), traits, "Main");
    }

    @Override
    protected float staminaCost() {
        return 1f;
    }

    @Override
    protected String ActivationType() {
        return "TOGGLE";
    }

    @Override
    protected void disablePowerSpecific(ServerPlayerEntity player) {

    }

    @Override
    protected void executeLogic(ServerPlayerEntity player, boolean activate, float stamina) {
        ServerPlayerEntity target = PlayerUtils.getPlayerLookedAt(player, 20.0D);
        if (target != null) {

             target.addStatusEffect(new StatusEffectInstance(
                     ModEffects.POWER_SUPPRESSION.value(),
                     2,
                     0,
                     true,
                     false
             ));
            target.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.GLOWING,
                    2,
                    0,
                    true,
                    false,
                    false
            ));

        }
    }

}