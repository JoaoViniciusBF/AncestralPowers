package dev.joaq.ancestralpowers.network.packet.s2c;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record ReviveProgressS2C(float progress, boolean isReviving, String downedPlayerName) {
    public static final Identifier ID = new Identifier("ancestralpowers", "revive_progress");
    
    public static void write(PacketByteBuf buf, float progress, boolean isReviving, String downedPlayerName) {
        buf.writeFloat(progress);
        buf.writeBoolean(isReviving);
        buf.writeString(downedPlayerName);
    }
    
    public static ReviveProgressS2C read(PacketByteBuf buf) {
        return new ReviveProgressS2C(buf.readFloat(), buf.readBoolean(), buf.readString(32767));
    }
}