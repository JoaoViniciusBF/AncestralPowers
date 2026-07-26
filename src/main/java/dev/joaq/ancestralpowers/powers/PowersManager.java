package dev.joaq.ancestralpowers.powers;

import dev.joaq.ancestralpowers.components.MyComponents;
import dev.joaq.ancestralpowers.components.PlayerTraits;
import dev.joaq.ancestralpowers.powers.main.*;
import dev.joaq.ancestralpowers.powers.secondary.*;
import net.minecraft.server.network.ServerPlayerEntity;

public class PowersManager {


    private static Power getPower(String power) {
        return switch (power) {
            case "SuperTeleporteMain" -> new SuperTeleportMainPower();
            case "Super Força" -> new StrengthPower();
            case "Suppressor" -> new SuppressionPower();
            case "SuperSpeed" -> new SuperSpeedPower();
            case "Scale" -> new IncreaseScalePower();
            case "Fireball" -> new FireballPower();
            case "Imortalidade" -> new ImortalPower();
            case "ArenaPower" -> new DimensionalArenaPower();
            case "Solar" -> new SolarAccelerationPower();
            case "Lunar" -> new LunarAccelerationPower();
            default -> null;
        };
    }
    private static Power getPowerSecondary(String power) {
        return switch (power) {
            case "SuperTeleporteSecondary" -> new SuperTeleportSecondaryPower();
            case "Scale" -> new DecreaseScalePower();
            case "SuperSpeed" -> new SuperSpeedPowerDecrease();
            case "Voo" -> new FlyPower();
            case "Velocidade" -> new SpeedPower();
            case "Teleporte" -> new TeleportPower();
            case "PersonalDimension" -> new PersonalDimensionPower();

            default -> null;
        };
    }

    public static void applyMainPower(ServerPlayerEntity player, String power, boolean activate, float stamina) {
        Power p = getPower(power);
        if (p != null) p.apply(player, activate, stamina);
    }


    public static void applyMovementPower(ServerPlayerEntity player, String power, boolean activate, float stamina) {
        Power p = getPowerSecondary(power);
        if (p != null) p.apply(player, activate, stamina);
    }


    public static void resetAll(ServerPlayerEntity player) {
        PlayerTraits traits = MyComponents.TRAITS.get(player);
        traits.setActPower_main(false);
        traits.setActPower_secondary(false);

        Power[] powers = {
                new StrengthPower(),
                new SuperSpeedPower(),
                new SuperSpeedPowerDecrease(),
                new SuperTeleportMainPower(),
                new SuperTeleportSecondaryPower(),
                new SuppressionPower(),
                new FireballPower(),
                new FlyPower(),
                new SpeedPower(),
                new IncreaseScalePower(),
                new DecreaseScalePower(),
                new TeleportPower(),
                new DimensionalArenaPower(),
                new SolarAccelerationPower(),
                new LunarAccelerationPower()
        };
        for (Power p : powers) {
            p.reset(player);
        }

    }


}
