package dev.joaq.ancestralpowers.networking.packet.c2s;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record ToggleRPayload(boolean value) {
    public static final Identifier ID = new Identifier("ancestralpowers", "toggle_r");
    
    public static void write(PacketByteBuf buf, boolean value) {
        buf.writeBoolean(value);
    }
    
    public static ToggleRPayload read(PacketByteBuf buf) {
        return new ToggleRPayload(buf.readBoolean());
    }
}
