package dev.joaq.ancestralpowers.npc;

import dev.joaq.ancestralpowers.registry.ModEntities;
import dev.joaq.ancestralpowers.skin.SkinManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class NPCManager {
    
    private static final Map<UUID, NPCEntity> ACTIVE_ENTITIES = new HashMap<>();
    
    public static NPCData createNPC(ServerPlayerEntity owner, NPCType type) {
        NPCData npcData = new NPCData(owner, type);
        
        NPCComponent component = NPCComponent.get(owner);
        component.addNPC(npcData);
        
        return npcData;
    }
    
    public static NPCEntity spawnNPC(ServerWorld world, NPCData npcData) {
        NPCEntity entity = new NPCEntity(ModEntities.NPC_ENTITY, world, npcData);
        
        world.spawnEntity(entity);
        
        ACTIVE_ENTITIES.put(npcData.getNpcId(), entity);
        npcData.setEntityUuid(entity.getUuid());
        
        return entity;
    }
    
    public static NPCData createAndSpawnNPC(ServerPlayerEntity owner, NPCType type) {
        NPCData npcData = createNPC(owner, type);
        spawnNPC(owner.getServerWorld(), npcData);
        return npcData;
    }
    
    public static NPCData createNPCWithSkin(ServerPlayerEntity owner, NPCType type, String skinName) {
        NPCData npcData = createNPC(owner, type);
        
        SkinManager.SkinData skin = SkinManager.getSkin(skinName);
        if (skin != null) {
            npcData.setSkinName(skinName);
            npcData.setSkinValue(skin.value);
            npcData.setSkinSignature(skin.signature);
        } else {
            npcData.setSkinName(owner.getName().getString());
        }
        
        return npcData;
    }
    
    public static NPCData createAndSpawnNPCWithSkin(ServerPlayerEntity owner, NPCType type, String skinName) {
        NPCData npcData = createNPCWithSkin(owner, type, skinName);
        spawnNPC(owner.getServerWorld(), npcData);
        return npcData;
    }
    
    public static void removeNPC(ServerPlayerEntity owner, UUID npcId) {
        NPCComponent component = NPCComponent.get(owner);
        NPCData npcData = component.getNPC(npcId);
        
        if (npcData != null) {
            if (npcData.getEntityUuid() != null) {
                NPCEntity entity = ACTIVE_ENTITIES.get(npcId);
                if (entity != null) {
                    entity.discard();
                    ACTIVE_ENTITIES.remove(npcId);
                }
            }
            
            component.removeNPC(npcId);
        }
    }

    public static void removeNPC(ServerWorld world, UUID ownerUuid, UUID npcId) {
        ServerPlayerEntity owner = world.getServer().getPlayerManager().getPlayer(ownerUuid);
        if (owner != null) {
            removeNPC(owner, npcId);
        }
    }
    
    public static void removeAllNPCs(ServerPlayerEntity owner) {
        NPCComponent component = NPCComponent.get(owner);
        List<NPCData> npcs = new ArrayList<>(component.getAllNPCs());
        
        for (NPCData npc : npcs) {
            removeNPC(owner, npc.getNpcId());
        }
    }
    
    public static void removeAllNPCsByType(ServerPlayerEntity owner, NPCType type) {
        NPCComponent component = NPCComponent.get(owner);
        List<NPCData> npcs = component.getNPCsByType(type);
        
        for (NPCData npc : npcs) {
            removeNPC(owner, npc.getNpcId());
        }
    }
    
    public static boolean startControlling(ServerPlayerEntity player, UUID npcId) {
        NPCComponent component = NPCComponent.get(player);
        NPCData npcData = component.getNPC(npcId);
        
        if (npcData != null && npcData.getOwnerUuid().equals(player.getUuid())) {
            component.setControlledNPCId(npcId);
            player.sendMessage(Text.literal("§aAgora controlando NPC: " + npcData.getType().getId()), true);
            return true;
        }
        
        return false;
    }
    
    public static void stopControlling(ServerPlayerEntity player) {
        NPCComponent component = NPCComponent.get(player);
        component.clearControl();
        player.sendMessage(Text.literal("§cControle do NPC liberado"), true);
    }
    
    public static void teleportToNPC(ServerPlayerEntity player, UUID npcId) {
        NPCComponent component = NPCComponent.get(player);
        NPCData npcData = component.getNPC(npcId);
        
        if (npcData != null && npcData.getEntityUuid() != null) {
            NPCEntity entity = ACTIVE_ENTITIES.get(npcId);
            if (entity != null) {
                player.teleport(entity.getX(), entity.getY(), entity.getZ());
            }
        }
    }
    
    public static void teleportNPCToPlayer(ServerPlayerEntity player, UUID npcId) {
        NPCComponent component = NPCComponent.get(player);
        NPCData npcData = component.getNPC(npcId);
        
        if (npcData != null && npcData.getEntityUuid() != null) {
            NPCEntity entity = ACTIVE_ENTITIES.get(npcId);
            if (entity != null) {
                entity.teleport(player.getX(), player.getY(), player.getZ());
                npcData.setPosition(player.getBlockPos());
                npcData.setYaw(player.getYaw());
                npcData.setPitch(player.getPitch());
            }
        }
    }
    
    public static void updateNPCFromPlayer(ServerPlayerEntity player, UUID npcId) {
        NPCComponent component = NPCComponent.get(player);
        NPCData npcData = component.getNPC(npcId);
        
        if (npcData != null) {
            npcData.setHealth(player.getHealth());
            npcData.setFoodLevel(player.getHungerManager().getFoodLevel());
            npcData.setSaturation(player.getHungerManager().getSaturationLevel());
            npcData.setXpLevel(player.experienceLevel);
            npcData.setXpProgress(player.experienceProgress);
            npcData.setTotalXp(player.totalExperience);
            npcData.setPosition(player.getBlockPos());
            npcData.setYaw(player.getYaw());
            npcData.setPitch(player.getPitch());
        }
    }
    
    public static NPCEntity getActiveEntity(UUID npcId) {
        return ACTIVE_ENTITIES.get(npcId);
    }
    
    public static void unregisterEntity(UUID npcId) {
        ACTIVE_ENTITIES.remove(npcId);
    }

    public static void updateHealth(UUID npcId, UUID ownerUuid, ServerWorld world, float health) {
        ServerPlayerEntity owner = world.getServer().getPlayerManager().getPlayer(ownerUuid);
        if (owner != null) {
            NPCComponent component = NPCComponent.get(owner);
            NPCData npcData = component.getNPC(npcId);
            if (npcData != null) {
                npcData.setHealth(health);
            }
        }
    }
    
    public static List<NPCData> getAllNPCs(ServerPlayerEntity player) {
        return NPCComponent.get(player).getAllNPCs();
    }
    
    public static int getNPCCount(ServerPlayerEntity player) {
        return NPCComponent.get(player).getNPCCount();
    }
    
    public static int getNPCCountByType(ServerPlayerEntity player, NPCType type) {
        return NPCComponent.get(player).getNPCCountByType(type);
    }
}
