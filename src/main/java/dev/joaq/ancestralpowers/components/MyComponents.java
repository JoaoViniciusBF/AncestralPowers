package dev.joaq.ancestralpowers.components;

import dev.joaq.ancestralpowers.AncestralPowers;
import net.minecraft.util.Identifier;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;

public class MyComponents implements EntityComponentInitializer {

    public static final ComponentKey<PlayerTraits> TRAITS =
            ComponentRegistryV3.INSTANCE.getOrCreate(
                    new Identifier(AncestralPowers.MOD_ID, "player_traits"), PlayerTraits.class);


    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(TRAITS, player -> new PlayerTraitsComponent() {

        }, RespawnCopyStrategy.ALWAYS_COPY);
    }
}