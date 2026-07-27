package dev.joaq.ancestralpowers.networking.packet.c2s;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import static dev.joaq.ancestralpowers.AncestralPowers.MOD_ID;

public record PersonalDimensionCounterPayload(Integer totalDirtBlocksBroken) {
    public static final Identifier ID = new Identifier(MOD_ID, "dirt_broken");
    
    public static void write(PacketByteBuf buf, Integer totalDirtBlocksBroken) {
        buf.writeInt(totalDirtBlocksBroken);
    }
    
    public static PersonalDimensionCounterPayload read(PacketByteBuf buf) {
        return new PersonalDimensionCounterPayload(buf.readInt());
    }
}
