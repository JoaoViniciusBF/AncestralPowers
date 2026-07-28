package dev.joaq.ancestralpowers.npc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.*;
import java.util.stream.Collectors;

public class NPCComponentImpl implements NPCComponent {
    
    private final Map<UUID, NPCData> npcs = new HashMap<>();
    private UUID controlledNPCId = null;
    
    public NPCComponentImpl(PlayerEntity provider) {
    }
    
    @Override
    public List<NPCData> getAllNPCs() {
        return new ArrayList<>(npcs.values());
    }
    
    @Override
    public void addNPC(NPCData npc) {
        npcs.put(npc.getNpcId(), npc);
    }
    
    @Override
    public void removeNPC(UUID npcId) {
        npcs.remove(npcId);
        if (npcId != null && npcId.equals(controlledNPCId)) {
            controlledNPCId = null;
        }
    }
    
    @Override
    public NPCData getNPC(UUID npcId) {
        return npcs.get(npcId);
    }
    
    @Override
    public List<NPCData> getNPCsByType(NPCType type) {
        return npcs.values().stream()
                .filter(npc -> npc.getType() == type)
                .collect(Collectors.toList());
    }
    
    @Override
    public UUID getControlledNPCId() {
        return controlledNPCId;
    }
    
    @Override
    public void setControlledNPCId(UUID npcId) {
        if (npcId == null || npcs.containsKey(npcId)) {
            this.controlledNPCId = npcId;
            if (npcId != null) {
                NPCData npc = npcs.get(npcId);
                if (npc != null) {
                    npc.setLastControlledAt(System.currentTimeMillis());
                }
            }
        }
    }
    
    @Override
    public boolean isControllingNPC() {
        return controlledNPCId != null;
    }
    
    @Override
    public void clearControl() {
        this.controlledNPCId = null;
    }
    
    @Override
    public int getNPCCount() {
        return npcs.size();
    }
    
    @Override
    public int getNPCCountByType(NPCType type) {
        return (int) npcs.values().stream()
                .filter(npc -> npc.getType() == type)
                .count();
    }
    
    @Override
    public void readFromNbt(NbtCompound tag) {
        npcs.clear();
        
        if (tag.contains("npcs")) {
            NbtList npcsList = tag.getList("npcs", 10);
            for (int i = 0; i < npcsList.size(); i++) {
                NPCData npc = NPCData.fromNbt(npcsList.getCompound(i));
                npcs.put(npc.getNpcId(), npc);
            }
        }
        
        if (tag.containsUuid("controlledNPCId")) {
            this.controlledNPCId = tag.getUuid("controlledNPCId");
        }
    }
    
    @Override
    public void writeToNbt(NbtCompound tag) {
        NbtList npcsList = new NbtList();
        for (NPCData npc : npcs.values()) {
            npcsList.add(npc.toNbt());
        }
        tag.put("npcs", npcsList);
        
        if (controlledNPCId != null) {
            tag.putUuid("controlledNPCId", controlledNPCId);
        }
    }
}
