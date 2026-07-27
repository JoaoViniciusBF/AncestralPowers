package dev.joaq.ancestralpowers.components;

import dev.joaq.ancestralpowers.AncestralPowers;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;


public class PersonalDimensionCounter extends PersistentState {
    public int personalDimensionCount = 0;

    public PersonalDimensionCounter() {}

    public PersonalDimensionCounter(int personalDimensionCount) {
        this.personalDimensionCount = personalDimensionCount;
    }

    public int getCount() {
        return personalDimensionCount;
    }

    public int incrementAndGet() {
        personalDimensionCount++;
        markDirty();
        return personalDimensionCount;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("personalDimensionCount", personalDimensionCount);
        return nbt;
    }

    public static PersonalDimensionCounter createFromNbt(NbtCompound nbt) {
        PersonalDimensionCounter state = new PersonalDimensionCounter();
        state.personalDimensionCount = nbt.getInt("personalDimensionCount");
        return state;
    }

    public static PersonalDimensionCounter getServerState(MinecraftServer server) {
         ServerWorld serverWorld = server.getWorld(World.OVERWORLD);
         assert serverWorld != null;
         String key = AncestralPowers.MOD_ID + "_personal_dimension";
         PersistentState.Type<PersonalDimensionCounter> type = new PersistentState.Type<>(
                 () -> new PersonalDimensionCounter(),
                 PersonalDimensionCounter::createFromNbt,
                 null
         );
         return serverWorld.getPersistentStateManager().getOrCreate(type, key);
     }
}
