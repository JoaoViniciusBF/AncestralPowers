package dev.joaq.ancestralpowers.networking;

import dev.joaq.ancestralpowers.networking.packet.s2c.StaminaSyncPayload;
import dev.joaq.ancestralpowers.networking.packet.s2c.DownedStateSyncS2C;
import dev.joaq.ancestralpowers.networking.packet.s2c.ReviveProgressS2C;
import dev.joaq.ancestralpowers.networking.packet.s2c.SummonSkeletonEffectPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class ModPacketsS2C {
    public static void register() {
    }

    public static void sendStaminaSync(ServerPlayerEntity player, float currentStamina, float maxStamina) {
        PacketByteBuf buf = net.fabricmc.fabric.api.networking.v1.PacketByteBufs.create();
        StaminaSyncPayload.write(buf, currentStamina, maxStamina);
        ServerPlayNetworking.send(player, StaminaSyncPayload.ID, buf);
    }

    public static void sendDownedStateSync(ServerPlayerEntity player, boolean isDowned, int bleedoutTimeRemaining) {
        PacketByteBuf buf = net.fabricmc.fabric.api.networking.v1.PacketByteBufs.create();
        DownedStateSyncS2C.write(buf, isDowned, bleedoutTimeRemaining);
        ServerPlayNetworking.send(player, DownedStateSyncS2C.ID, buf);
    }

    public static void sendReviveProgress(ServerPlayerEntity player, float progress, boolean isReviving, String targetName) {
        PacketByteBuf buf = net.fabricmc.fabric.api.networking.v1.PacketByteBufs.create();
        ReviveProgressS2C.write(buf, progress, isReviving, targetName);
        ServerPlayNetworking.send(player, ReviveProgressS2C.ID, buf);
    }

    public static void sendSummonSkeletonEffect(ServerPlayerEntity player, List<Vec3d> spawnPositions) {
        PacketByteBuf buf = net.fabricmc.fabric.api.networking.v1.PacketByteBufs.create();
        SummonSkeletonEffectPayload.write(buf, spawnPositions);
        ServerPlayNetworking.send(player, SummonSkeletonEffectPayload.ID, buf);
    }
}