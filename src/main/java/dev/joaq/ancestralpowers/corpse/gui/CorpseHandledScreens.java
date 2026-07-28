package dev.joaq.ancestralpowers.corpse.gui;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class CorpseHandledScreens {

    public static final ScreenHandlerType<CorpseGuis.CorpseScreenHandler> CORPSE_SCREEN_HANDLER = Registry.register(
            Registries.SCREEN_HANDLER,
            new Identifier("ancestralpowers", "corpse"),
            new ScreenHandlerType<CorpseGuis.CorpseScreenHandler>(
                (syncId, inventory) -> new CorpseGuis.CorpseScreenHandler(syncId, inventory),
                FeatureSet.empty()
            )
    );

    public static void register() {
    }
}