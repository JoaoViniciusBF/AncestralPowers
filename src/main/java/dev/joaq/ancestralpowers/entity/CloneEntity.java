package dev.joaq.ancestralpowers.entity;

import dev.joaq.ancestralpowers.npc.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class CloneEntity {
    
    public static NPCEntity createClone(ServerPlayerEntity player, int cloneNumber) {
        ServerWorld world = player.getServerWorld();
        
        NPCData npcData = NPCManager.createNPC(player, NPCType.CLONE);
        npcData.setPosition(player.getBlockPos());
        npcData.setYaw(player.getYaw());
        npcData.setPitch(player.getPitch());
        
        NPCEntity npc = NPCManager.spawnNPC(world, npcData);
        npc.setCustomName(Text.literal("§6Clone #" + cloneNumber + " §7(" + player.getName().getString() + ")"));
        npc.setCustomNameVisible(true);
        
        return npc;
    }
    
    public static NPCEntity createCloneAt(ServerPlayerEntity player, int cloneNumber, BlockPos pos, String dimension) {
        ServerWorld world = player.getServerWorld();
        
        NPCData npcData = NPCManager.createNPC(player, NPCType.CLONE);
        npcData.setPosition(pos);
        
        NPCEntity npc = NPCManager.spawnNPC(world, npcData);
        npc.setCustomName(Text.literal("§6Clone #" + cloneNumber + " §7(" + player.getName().getString() + ")"));
        npc.setCustomNameVisible(true);
        
        return npc;
    }
    
    public static void removeCloneAt(ServerWorld world, BlockPos pos) {
        world.getEntitiesByClass(NPCEntity.class, 
            new net.minecraft.util.math.Box(pos).expand(2, 4, 2),
            entity -> entity.getCustomName() != null && 
                      entity.getCustomName().getString().contains("Clone #")
        ).forEach(entity -> {
            NPCManager.unregisterEntity(entity.getNpcDataId());
            entity.discard();
        });
    }
}
