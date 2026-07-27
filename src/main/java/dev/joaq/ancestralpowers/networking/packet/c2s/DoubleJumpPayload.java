package dev.joaq.ancestralpowers.networking.packet.c2s;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record DoubleJumpPayload() {
    public static final Identifier ID = new Identifier("ancestralpowers", "double_jump");
    
    public static void write(PacketByteBuf buf) {
    }
    
    public static DoubleJumpPayload read(PacketByteBuf buf) {
        return new DoubleJumpPayload();
    }
}
