package dev.joaq.ancestralpowers.events;

import dev.joaq.ancestralpowers.components.MyComponents;
import dev.joaq.ancestralpowers.components.PlayerTraits;
import dev.joaq.ancestralpowers.util.DownedStateTracker;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class DownedDisconnectHandler {

    public static void register() {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            if (player != null) {
                PlayerTraits traits = MyComponents.TRAITS.get(player);
                if (traits.getIsDowned()) {
                    DownedStateTracker.setDowned(player.getUuid(), false);
                    traits.setIsDowned(false);
                    traits.setBleedoutTimer(0);
                    traits.clearReviverUuid();
                    traits.setReviveProgress(0);
                }
            }
        });
    }
}