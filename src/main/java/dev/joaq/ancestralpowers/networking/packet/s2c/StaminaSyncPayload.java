package dev.joaq.ancestralpowers.networking.packet.s2c;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record StaminaSyncPayload(float currentStamina, float maxStamina) implements CustomPayload {
    public static final Identifier ID = Identifier.of("ancestralpowers", "stamina_sync");
    public static final CustomPayload.Id<StaminaSyncPayload> PAYLOAD_ID = new CustomPayload.Id<>(ID);
    public static final PacketCodec<RegistryByteBuf, StaminaSyncPayload> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.FLOAT, StaminaSyncPayload::currentStamina,
                    PacketCodecs.FLOAT, StaminaSyncPayload::maxStamina,
                    StaminaSyncPayload::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return PAYLOAD_ID;
    }
}