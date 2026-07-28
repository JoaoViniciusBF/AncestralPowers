package dev.joaq.ancestralpowers.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class CloneEntity {
    
    public static ArmorStandEntity createClone(ServerPlayerEntity player, int cloneNumber) {
        ServerWorld world = player.getServerWorld();
        Vec3d pos = player.getPos();
        
        ArmorStandEntity clone = new ArmorStandEntity(world, pos.x, pos.y, pos.z);
        
        clone.setInvisible(false);
        clone.setNoGravity(true);
        clone.setInvulnerable(true);
        clone.setCustomName(Text.literal("§6Clone #" + cloneNumber + " §7(" + player.getName().getString() + ")"));
        clone.setCustomNameVisible(true);
        
        clone.setBodyYaw(player.bodyYaw);
        clone.setYaw(player.getYaw());
        clone.setPitch(player.getPitch());
        
        clone.setShowArms(true);
        clone.setHideBasePlate(true);
        
        NbtCompound nbt = new NbtCompound();
        clone.writeNbt(nbt);
        nbt.putBoolean("ShowArms", true);
        nbt.putBoolean("NoBasePlate", true);
        nbt.putBoolean("Marker", false);
        
        GameProfile profile = player.getGameProfile();
        NbtCompound ownerNbt = new NbtCompound();
        ownerNbt.putString("Name", profile.getName());
        ownerNbt.putString("Id", profile.getId().toString());
        nbt.put("Owner", ownerNbt);
        
        clone.readNbt(nbt);
        
        if (player.getInventory().armor.get(3) != null) {
            clone.equipStack(net.minecraft.entity.EquipmentSlot.HEAD, player.getInventory().armor.get(3).copy());
        }
        if (player.getInventory().armor.get(2) != null) {
            clone.equipStack(net.minecraft.entity.EquipmentSlot.CHEST, player.getInventory().armor.get(2).copy());
        }
        if (player.getInventory().armor.get(1) != null) {
            clone.equipStack(net.minecraft.entity.EquipmentSlot.LEGS, player.getInventory().armor.get(1).copy());
        }
        if (player.getInventory().armor.get(0) != null) {
            clone.equipStack(net.minecraft.entity.EquipmentSlot.FEET, player.getInventory().armor.get(0).copy());
        }
        
        if (!player.getMainHandStack().isEmpty()) {
            clone.equipStack(net.minecraft.entity.EquipmentSlot.MAINHAND, player.getMainHandStack().copy());
        }
        if (!player.getOffHandStack().isEmpty()) {
            clone.equipStack(net.minecraft.entity.EquipmentSlot.OFFHAND, player.getOffHandStack().copy());
        }
        
        world.spawnEntity(clone);
        
        return clone;
    }
    
    public static void removeCloneAt(ServerWorld world, BlockPos pos) {
        world.getEntitiesByClass(ArmorStandEntity.class, 
            net.minecraft.util.math.Box.of(Vec3d.ofCenter(pos), 2, 4, 2),
            entity -> entity.getCustomName() != null && 
                      entity.getCustomName().getString().contains("Clone #")
        ).forEach(entity -> entity.discard());
    }
}
