package dev.joaq.ancestralpowers.networking.packet.c2s;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record OffhandAttackC2SPayload(int targetId) {
    public static final Identifier ID = new Identifier("ancestralpowers", "offhand_attack");
    
    public static void write(PacketByteBuf buf, int targetId) {
        buf.writeInt(targetId);
    }
    
    public static OffhandAttackC2SPayload read(PacketByteBuf buf) {
        return new OffhandAttackC2SPayload(buf.readInt());
    }
}
