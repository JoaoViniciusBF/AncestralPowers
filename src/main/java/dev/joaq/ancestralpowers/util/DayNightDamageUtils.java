package dev.joaq.ancestralpowers.util;

import net.minecraft.world.World;

public class DayNightDamageUtils {
    public static float getSolarMultiplier(World world) {
        long timeOfDay = world.getTimeOfDay() % 24000;
        float dayStrength = getDayStrength(timeOfDay);

        return 0.3f + (dayStrength * 1.7f);
    }

    public static float getLunarMultiplier(World world) {
        long timeOfDay = world.getTimeOfDay() % 24000;
        float dayStrength = getDayStrength(timeOfDay);
        float nightStrength = 1.0f - dayStrength;

        return 0.3f + (nightStrength * 1.7f);
    }

    private static float getDayStrength(long timeOfDay) {
        double angle = ((timeOfDay - 6000) / 24000.0) * 2.0 * Math.PI;
        double value = (Math.cos(angle) + 1.0) / 2.0;

        return (float) Math.max(0.0, Math.min(1.0, value));
    }
}
