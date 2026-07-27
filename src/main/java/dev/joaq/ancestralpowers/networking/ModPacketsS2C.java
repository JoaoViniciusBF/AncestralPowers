package dev.joaq.ancestralpowers.networking;

import dev.joaq.ancestralpowers.networking.packet.s2c.StaminaSyncPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ModPacketsS2C {
    public static void register() {
    }

    public static void sendStaminaSync(ServerPlayerEntity player, float currentStamina, float maxStamina) {
        PacketByteBuf buf = net.fabricmc.fabric.api.networking.v1.PacketByteBufs.create();
        StaminaSyncPayload.write(buf, currentStamina, maxStamina);
        ServerPlayNetworking.send(player, StaminaSyncPayload.ID, buf);
    }
}