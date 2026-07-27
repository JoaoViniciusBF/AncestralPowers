package dev.joaq.ancestralpowers.networking.packet.c2s;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record ToggleGPayload(boolean value) {
    public static final Identifier ID = new Identifier("ancestralpowers", "toggle_g");
    
    public static void write(PacketByteBuf buf, boolean value) {
        buf.writeBoolean(value);
    }
    
    public static ToggleGPayload read(PacketByteBuf buf) {
        return new ToggleGPayload(buf.readBoolean());
    }
}
