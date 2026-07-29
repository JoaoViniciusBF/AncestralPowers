package dev.joaq.ancestralpowers.networking;

import dev.joaq.ancestralpowers.networking.packet.s2c.StaminaSyncPayload;
import dev.joaq.ancestralpowers.networking.packet.s2c.DownedStateSyncS2C;
import dev.joaq.ancestralpowers.networking.packet.s2c.ReviveProgressS2C;
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

    public static void sendDownedStateSync(ServerPlayerEntity player, boolean isDowned, int bleedoutTime) {
        PacketByteBuf buf = net.fabricmc.fabric.api.networking.v1.PacketByteBufs.create();
        DownedStateSyncS2C.write(buf, isDowned, bleedoutTime);
        ServerPlayNetworking.send(player, DownedStateSyncS2C.ID, buf);
    }

    public static void sendReviveProgress(ServerPlayerEntity player, float progress, boolean isReviving, String targetName) {
        PacketByteBuf buf = net.fabricmc.fabric.api.networking.v1.PacketByteBufs.create();
        ReviveProgressS2C.write(buf, progress, isReviving, targetName);
        ServerPlayNetworking.send(player, ReviveProgressS2C.ID, buf);
    }
}