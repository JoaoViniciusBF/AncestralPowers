package dev.joaq.ancestralpowers.networking.packet.s2c;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record StaminaSyncPayload(float currentStamina, float maxStamina) {
    public static final Identifier ID = new Identifier("ancestralpowers", "stamina_sync");
    
    public static void write(PacketByteBuf buf, float currentStamina, float maxStamina) {
        buf.writeFloat(currentStamina);
        buf.writeFloat(maxStamina);
    }
    
    public static StaminaSyncPayload read(PacketByteBuf buf) {
        return new StaminaSyncPayload(buf.readFloat(), buf.readFloat());
    }
}