package dev.joaq.ancestralpowers.network.packet.c2s;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record ReviveStartC2S(boolean start, int targetEntityId) {
    public static final Identifier ID = new Identifier("ancestralpowers", "revive_start");
    
    public static void write(PacketByteBuf buf, boolean start, int targetEntityId) {
        buf.writeBoolean(start);
        buf.writeInt(targetEntityId);
    }
    
    public static ReviveStartC2S read(PacketByteBuf buf) {
        return new ReviveStartC2S(buf.readBoolean(), buf.readInt());
    }
}