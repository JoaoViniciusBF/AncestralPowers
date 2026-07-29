package dev.joaq.ancestralpowers.networking.packet.c2s;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record ReviveStartC2S(int targetEntityId) {
    public static final Identifier ID = new Identifier("ancestralpowers", "revive_start");
    
    public static void write(PacketByteBuf buf, int targetEntityId) {
        buf.writeInt(targetEntityId);
    }
    
    public static ReviveStartC2S read(PacketByteBuf buf) {
        return new ReviveStartC2S(buf.readInt());
    }
}