package dev.joaq.ancestralpowers.networking.packet.c2s;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record DoubleJumpPayload() implements CustomPayload {
    public static final Identifier ID = Identifier.of("ancestralpowers", "double_jump");
    public static final CustomPayload.Id<DoubleJumpPayload> PAYLOAD_ID = new CustomPayload.Id<>(ID);
    public static final PacketCodec<RegistryByteBuf, DoubleJumpPayload> CODEC = PacketCodec.unit(new DoubleJumpPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return PAYLOAD_ID;
    }
}
