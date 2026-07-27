package dev.joaq.ancestralpowers.components;

import dev.joaq.ancestralpowers.AncestralPowers;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

public class DimensionalArenaCounter extends PersistentState {
    public int dimensionalArenaCounter = 0;

    public DimensionalArenaCounter() {}

    public DimensionalArenaCounter(int dimensionalArenaCounter) {
        this.dimensionalArenaCounter = dimensionalArenaCounter;
    }

    public int getCount() {
        return dimensionalArenaCounter;
    }

    public int incrementAndGet() {
        dimensionalArenaCounter++;
        markDirty();
        return dimensionalArenaCounter;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("dimensionalArenaCounter", dimensionalArenaCounter);
        return nbt;
    }

    public static DimensionalArenaCounter createFromNbt(NbtCompound nbt) {
        DimensionalArenaCounter state = new DimensionalArenaCounter();
        state.dimensionalArenaCounter = nbt.getInt("dimensionalArenaCounter");
        return state;
    }

    private static final PersistentState.Type<DimensionalArenaCounter> TYPE = 
            new PersistentState.Type<>(
                () -> new DimensionalArenaCounter(),
                DimensionalArenaCounter::createFromNbt,
                null
            );

    public static DimensionalArenaCounter getServerState(MinecraftServer server) {
        ServerWorld serverWorld = server.getWorld(World.OVERWORLD);
        assert serverWorld != null;
        String key = AncestralPowers.MOD_ID + "_dimensional_arena";
        return serverWorld.getPersistentStateManager().getOrCreate(TYPE, key);
    }
}
