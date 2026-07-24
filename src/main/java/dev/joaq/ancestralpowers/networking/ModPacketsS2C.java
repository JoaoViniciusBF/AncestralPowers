package dev.joaq.ancestralpowers.networking;

import dev.joaq.ancestralpowers.networking.packet.s2c.StaminaSyncPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public class ModPacketsS2C {
    public static void register() {
    }

    public static void sendStaminaSync(ServerPlayerEntity player, float currentStamina, float maxStamina) {
        ServerPlayNetworking.send(player, new StaminaSyncPayload(currentStamina, maxStamina));
    }
}