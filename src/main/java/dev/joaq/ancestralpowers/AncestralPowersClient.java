package dev.joaq.ancestralpowers;

import dev.joaq.ancestralpowers.client.DoubleJumpHandler;
import dev.joaq.ancestralpowers.client.ModKeyBinds;
import dev.joaq.ancestralpowers.client.StaminaHudOverlay;
import dev.joaq.ancestralpowers.commands.InventoryPosCommand;
import dev.joaq.ancestralpowers.networking.packet.c2s.ToggleGPayload;
import dev.joaq.ancestralpowers.networking.packet.c2s.ToggleRPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;

public class AncestralPowersClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModKeyBinds.registerKeyBinds();
        StaminaHudOverlay.register();
        
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
            }
        });

        net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            InventoryPosCommand.register(dispatcher);
        });
    }
}
