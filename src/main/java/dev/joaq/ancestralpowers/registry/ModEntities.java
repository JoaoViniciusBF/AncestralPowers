package dev.joaq.ancestralpowers.registry;

import dev.joaq.ancestralpowers.entiy.CustomFireballEntity;
import dev.joaq.ancestralpowers.npc.NPCEntity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<CustomFireballEntity> CUSTOM_FIREBALL = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("ancestralpowers", "custom_fireball"),
            EntityType.Builder.<CustomFireballEntity>create(CustomFireballEntity::new, SpawnGroup.MISC)
                    .build()
    );

    public static final EntityType<NPCEntity> NPC_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("ancestralpowers", "npc_entity"),
            EntityType.Builder.<NPCEntity>create(NPCEntity::new, SpawnGroup.MISC)
                    .setDimensions(0.6f, 1.8f)
                    .maxTrackingRange(10)
                    .trackingTickInterval(3)
                    .build()
    );

    public static void register() {
    }
}