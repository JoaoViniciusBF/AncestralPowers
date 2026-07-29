package dev.joaq.ancestralpowers.networking.packet.s2c;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public record ReviveProgressS2C(float progress, boolean isReviving, String targetName) {
    public static final Identifier ID = new Identifier("ancestralpowers", "revive_progress");
    
    public static void write(PacketByteBuf buf, float progress, boolean isReviving, String targetName) {
        buf.writeFloat(progress);
        buf.writeBoolean(isReviving);
        buf.writeString(targetName);
    }
    
    public static ReviveProgressS2C read(PacketByteBuf buf) {
        return new ReviveProgressS2C(buf.readFloat(), buf.readBoolean(), buf.readString(32767));
    }

    public static void sendToTracking(ServerPlayerEntity player, float progress, boolean isReviving, String targetName) {
        PacketByteBuf buf = net.fabricmc.fabric.api.networking.v1.PacketByteBufs.create();
        write(buf, progress, isReviving, targetName);
        ServerPlayNetworking.send(player, ID, buf);
    }
}