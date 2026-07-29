package dev.joaq.ancestralpowers.networking.packet.s2c;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public record SummonSkeletonEffectPayload(List<Vec3d> spawnPositions) {
    public static final Identifier ID = new Identifier("ancestralpowers", "summon_skeleton_effect");

    public static void write(PacketByteBuf buf, List<Vec3d> positions) {
        buf.writeInt(positions.size());
        for (Vec3d pos : positions) {
            buf.writeDouble(pos.x);
            buf.writeDouble(pos.y);
            buf.writeDouble(pos.z);
        }
    }

    public static SummonSkeletonEffectPayload read(PacketByteBuf buf) {
        int count = buf.readInt();
        List<Vec3d> positions = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            positions.add(new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble()));
        }
        return new SummonSkeletonEffectPayload(positions);
    }
}