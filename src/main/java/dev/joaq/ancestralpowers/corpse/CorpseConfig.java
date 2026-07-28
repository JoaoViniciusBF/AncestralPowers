package dev.joaq.ancestralpowers.corpse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

public class CorpseConfig {

    public static boolean onlyOwnerAccess = false;
    public static boolean skeletonAccess = false;
    public static int corpseDespawnTime = 20 * 30; // 30 seconds
    public static int corpseForceDespawnTime = -1;
    public static int corpseSkeletonTime = 20 * 60 * 60; // 1 hour
    public static boolean spawnCorpseOnFace = false;
    public static boolean renderEquipment = true;
    public static boolean fallIntoVoid = false;
    public static boolean lavaDamage = false;
    public static int maxDeathAge = -1; // days

    private static final Properties props = new Properties();
    private static final File configFile = new File("config/corpse.properties");

    public static void load() {
        if (configFile.exists()) {
            try {
                props.load(Files.newInputStream(configFile.toPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        onlyOwnerAccess = Boolean.parseBoolean(props.getProperty("onlyOwnerAccess", "false"));
        skeletonAccess = Boolean.parseBoolean(props.getProperty("skeletonAccess", "false"));
        corpseDespawnTime = Integer.parseInt(props.getProperty("corpseDespawnTime", String.valueOf(20 * 30)));
        corpseForceDespawnTime = Integer.parseInt(props.getProperty("corpseForceDespawnTime", "-1"));
        corpseSkeletonTime = Integer.parseInt(props.getProperty("corpseSkeletonTime", String.valueOf(20 * 60 * 60)));
        spawnCorpseOnFace = Boolean.parseBoolean(props.getProperty("spawnCorpseOnFace", "false"));
        renderEquipment = Boolean.parseBoolean(props.getProperty("renderEquipment", "true"));
        fallIntoVoid = Boolean.parseBoolean(props.getProperty("fallIntoVoid", "false"));
        lavaDamage = Boolean.parseBoolean(props.getProperty("lavaDamage", "false"));
        maxDeathAge = Integer.parseInt(props.getProperty("maxDeathAge", "-1"));

        save();
    }

    public static void save() {
        props.setProperty("onlyOwnerAccess", String.valueOf(onlyOwnerAccess));
        props.setProperty("skeletonAccess", String.valueOf(skeletonAccess));
        props.setProperty("corpseDespawnTime", String.valueOf(corpseDespawnTime));
        props.setProperty("corpseForceDespawnTime", String.valueOf(corpseForceDespawnTime));
        props.setProperty("corpseSkeletonTime", String.valueOf(corpseSkeletonTime));
        props.setProperty("spawnCorpseOnFace", String.valueOf(spawnCorpseOnFace));
        props.setProperty("renderEquipment", String.valueOf(renderEquipment));
        props.setProperty("fallIntoVoid", String.valueOf(fallIntoVoid));
        props.setProperty("lavaDamage", String.valueOf(lavaDamage));
        props.setProperty("maxDeathAge", String.valueOf(maxDeathAge));
        try {
            configFile.getParentFile().mkdirs();
            props.store(Files.newOutputStream(configFile.toPath()), "Corpse Config");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}