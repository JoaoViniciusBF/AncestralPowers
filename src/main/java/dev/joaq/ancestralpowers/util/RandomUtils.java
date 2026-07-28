package dev.joaq.ancestralpowers.util;

import java.util.Random;

public class RandomUtils {
    private static final Random rand = new Random();

    private static final String[] mains = {"Super Força", "Imortalidade", "Fireball", "SuperTeleporteMain", "Scale", "SuperSpeed", "Suppressor", "ArenaPower", "Solar", "Lunar", "Clone", "Ascension"};
    private static final String[] movements = {"Voo", "Teleporte", "Velocidade", "PersonalDimension"};
    private static final String[] intelligences = {"Burro", "Inteligente", "Gênio"};

    public static String randomMain() {
        return mains[rand.nextInt(mains.length)];
    }

    // Recebe a trait principal para decidir o movimento
    public static String randomMovement(String mainTrait) {
        if ("SuperTeleporteMain".equals(mainTrait)) {
            return "SuperTeleporteSecondary";
        }
        if ("SuperSpeed".equals(mainTrait)) {
            return "SuperSpeed";
        }
        if ("Scale".equals(mainTrait)) {
            return "Scale";
        }
        return movements[rand.nextInt(movements.length)];
    }

    public static String randomIntelligence() {
        return intelligences[rand.nextInt(intelligences.length)];
    }
}

