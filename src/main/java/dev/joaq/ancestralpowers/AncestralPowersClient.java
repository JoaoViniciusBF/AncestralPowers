package dev.joaq.ancestralpowers;

import dev.joaq.ancestralpowers.client.DoubleJumpHandler;
import dev.joaq.ancestralpowers.client.DownedHudOverlay;
import dev.joaq.ancestralpowers.client.ModKeyBinds;
import dev.joaq.ancestralpowers.client.StaminaHudOverlay;
import dev.joaq.ancestralpowers.client.renderer.NPCEntityRenderer;
import dev.joaq.ancestralpowers.commands.InventoryPosCommand;
import dev.joaq.ancestralpowers.corpse.client.renderer.CorpseRenderer;
import dev.joaq.ancestralpowers.registry.ModCorpseEntities;
import dev.joaq.ancestralpowers.corpse.gui.CorpseHandledScreens;
import dev.joaq.ancestralpowers.corpse.gui.CorpseScreen;
import dev.joaq.ancestralpowers.networking.packet.c2s.ToggleGPayload;
import dev.joaq.ancestralpowers.networking.packet.c2s.ToggleRPayload;
import dev.joaq.ancestralpowers.registry.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;

public class AncestralPowersClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModKeyBinds.registerKeyBinds();
        StaminaHudOverlay.register();
        DownedHudOverlay.register();
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ModKeyBinds.R_KEY.wasPressed()) {
                PacketByteBuf buf = PacketByteBufs.create();
                ToggleRPayload.write(buf, true);
                ClientPlayNetworking.send(ToggleRPayload.ID, buf);
            }

            if (ModKeyBinds.G_KEY.wasPressed()) {
                PacketByteBuf buf = PacketByteBufs.create();
                ToggleGPayload.write(buf, true);
                ClientPlayNetworking.send(ToggleGPayload.ID, buf);
            }

            DoubleJumpHandler.onClientTick(client);
        });

        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && client.inGameHud != null) {
                StaminaHudOverlay.render(context, client.inGameHud, tickDelta);
                DownedHudOverlay.render(context, client.inGameHud, tickDelta);
            }
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            InventoryPosCommand.register(dispatcher);
        });

        EntityRendererRegistry.register(ModEntities.NPC_ENTITY, NPCEntityRenderer::new);
        EntityRendererRegistry.register(ModCorpseEntities.CORPSE_ENTITY, CorpseRenderer::new);
        ScreenRegistry.register(CorpseHandledScreens.CORPSE_SCREEN_HANDLER, CorpseScreen::new);
    }
}