package dev.joaq.ancestralpowers.registry;

import dev.joaq.ancestralpowers.corpse.entity.CorpseEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModCorpseEntities {
    public static final EntityType<CorpseEntity> CORPSE_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier("ancestralpowers", "corpse"),
            EntityType.Builder.<CorpseEntity>create(CorpseEntity::new, SpawnGroup.MISC)
                    .setDimensions(2F, 0.5F)
                    .maxTrackingRange(128)
                    .trackingTickInterval(1)
                    .build()
    );

    public static void register() {
    }
}