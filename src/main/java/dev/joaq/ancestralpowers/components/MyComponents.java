package dev.joaq.ancestralpowers.components;

import dev.joaq.ancestralpowers.AncestralPowers;
import dev.joaq.ancestralpowers.npc.NPCComponent;
import dev.joaq.ancestralpowers.npc.NPCComponentImpl;
import net.minecraft.util.Identifier;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;

public class MyComponents implements EntityComponentInitializer {

    public static ComponentKey<PlayerTraits> TRAITS;
    public static ComponentKey<NPCComponent> NPC_COMPONENT;

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        TRAITS = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(AncestralPowers.MOD_ID, "player_traits"), PlayerTraits.class);
        registry.registerForPlayers(TRAITS, player -> new PlayerTraitsComponent(), RespawnCopyStrategy.ALWAYS_COPY);
        
        NPC_COMPONENT = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(AncestralPowers.MOD_ID, "npc_component"), NPCComponent.class);
        registry.registerForPlayers(NPC_COMPONENT, NPCComponentImpl::new, RespawnCopyStrategy.ALWAYS_COPY);
    }
}