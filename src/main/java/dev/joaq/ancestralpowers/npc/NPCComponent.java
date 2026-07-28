package dev.joaq.ancestralpowers.npc;

import dev.joaq.ancestralpowers.components.MyComponents;
import net.minecraft.entity.player.PlayerEntity;
import dev.onyxstudios.cca.api.v3.component.Component;

import java.util.*;

public interface NPCComponent extends Component {
    
    List<NPCData> getAllNPCs();
    
    void addNPC(NPCData npc);
    
    void removeNPC(UUID npcId);
    
    NPCData getNPC(UUID npcId);
    
    List<NPCData> getNPCsByType(NPCType type);
    
    UUID getControlledNPCId();
    
    void setControlledNPCId(UUID npcId);
    
    boolean isControllingNPC();
    
    void clearControl();
    
    int getNPCCount();
    
    int getNPCCountByType(NPCType type);
    
    static NPCComponent get(PlayerEntity player) {
        return MyComponents.NPC_COMPONENT.get(player);
    }
}
