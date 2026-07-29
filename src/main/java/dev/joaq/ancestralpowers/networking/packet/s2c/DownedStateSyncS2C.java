package dev.joaq.ancestralpowers.networking.packet.s2c;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public record DownedStateSyncS2C(boolean isDowned, int bleedoutTimeRemaining) {
    public static final Identifier ID = new Identifier("ancestralpowers", "downed_state_sync");
    
    public static void write(PacketByteBuf buf, boolean isDowned, int bleedoutTimeRemaining) {
        buf.writeBoolean(isDowned);
        buf.writeInt(bleedoutTimeRemaining);
    }
    
    public static DownedStateSyncS2C read(PacketByteBuf buf) {
        return new DownedStateSyncS2C(buf.readBoolean(), buf.readInt());
    }

    public static void sendToTracking(ServerPlayerEntity player, boolean isDowned, int bleedoutTimeRemaining) {
        PacketByteBuf buf = net.fabricmc.fabric.api.networking.v1.PacketByteBufs.create();
        write(buf, isDowned, bleedoutTimeRemaining);
        ServerPlayNetworking.send(player, ID, buf);
    }
}