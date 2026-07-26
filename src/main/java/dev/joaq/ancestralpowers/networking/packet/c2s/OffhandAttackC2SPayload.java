package dev.joaq.ancestralpowers.networking.packet.c2s;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record OffhandAttackC2SPayload() implements CustomPayload {
    public static final CustomPayload.Id<OffhandAttackC2SPayload> ID =
        new CustomPayload.Id<>(Identifier.of("ancestralpowers", "offhand_attack"));
    public static final PacketCodec<PacketByteBuf, OffhandAttackC2SPayload> CODEC =
        PacketCodec.unit(new OffhandAttackC2SPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
