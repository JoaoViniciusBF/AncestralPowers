package dev.joaq.ancestralpowers;

import dev.joaq.ancestralpowers.commands.ModCommands;
import dev.joaq.ancestralpowers.components.PersonalDimensionCounter;
import dev.joaq.ancestralpowers.dimensions.ModDimensions;
import dev.joaq.ancestralpowers.events.PlayerDeathEvent;
import dev.joaq.ancestralpowers.events.PlayerJoinEvent;
import dev.joaq.ancestralpowers.events.PlayerPowersTickHandler;
import dev.joaq.ancestralpowers.networking.ModPacketsC2S;
import dev.joaq.ancestralpowers.networking.ModPacketsS2C;
import dev.joaq.ancestralpowers.offhand.OffhandMod;
import dev.joaq.ancestralpowers.registry.ModEffects;
import dev.joaq.ancestralpowers.registry.ModEntities;
import dev.joaq.ancestralpowers.registry.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AncestralPowers implements ModInitializer, ClientModInitializer, DedicatedServerModInitializer {
    public static final String MOD_ID = "ancestralpowers";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static Identifier identifier(String path) {
        return new Identifier(AncestralPowers.MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        ModPacketsC2S.register();
        ModPacketsS2C.register();
        ModDimensions.register();
        PlayerJoinEvent.register();
        PlayerPowersTickHandler.register();
        ModCommands.register();
        ModEntities.register();
        ModEffects.register();
        ModItems.register();
        PlayerDeathEvent.ImortalRegister();
        OffhandMod.init();
        System.out.println("ancestralpowers: AncestralPowers inicializado!");
    }


    @Override
    public void onInitializeClient() {

    }

    @Override
    public void onInitializeServer() {

    }
}
