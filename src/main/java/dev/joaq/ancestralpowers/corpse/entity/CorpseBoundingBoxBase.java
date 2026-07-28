package dev.joaq.ancestralpowers.corpse.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public abstract class CorpseBoundingBoxBase extends Entity {

    public CorpseBoundingBoxBase(EntityType<?> type, World world) {
        super(type, world);
    }

    public void recalculateBoundingBox() {
        Direction facing = Direction.fromRotation(getYaw());
        this.setBoundingBox(new Box(
                getX() - (facing.getOffsetX() != 0 ? 1.0 : 0.5),
                getY(),
                getZ() - (facing.getOffsetZ() != 0 ? 1.0 : 0.5),
                getX() + (facing.getOffsetX() != 0 ? 1.0 : 0.5),
                getY() + 0.5,
                getZ() + (facing.getOffsetZ() != 0 ? 1.0 : 0.5)
        ));
    }

    @Override
    public void tick() {
        super.tick();
        recalculateBoundingBox();
    }

    @Override
    public void setPosition(double x, double y, double z) {
        super.setPosition(x, y, z);
        recalculateBoundingBox();
    }
}