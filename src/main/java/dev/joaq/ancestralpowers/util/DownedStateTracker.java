package dev.joaq.ancestralpowers.util;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DownedStateTracker {
    private static final Set<UUID> DOWNED_PLAYERS = new HashSet<>();

    public static void setDowned(UUID playerUuid, boolean downed) {
        if (downed) DOWNED_PLAYERS.add(playerUuid);
        else DOWNED_PLAYERS.remove(playerUuid);
    }

    public static boolean isDowned(UUID playerUuid) {
        return DOWNED_PLAYERS.contains(playerUuid);
    }
}