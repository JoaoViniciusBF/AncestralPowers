package dev.joaq.ancestralpowers.networking.packet.c2s;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record ReviveCancelC2S() {
    public static final Identifier ID = new Identifier("ancestralpowers", "revive_cancel");
    
    public static void write(PacketByteBuf buf) {
    }
    
    public static ReviveCancelC2S read(PacketByteBuf buf) {
        return new ReviveCancelC2S();
    }
}