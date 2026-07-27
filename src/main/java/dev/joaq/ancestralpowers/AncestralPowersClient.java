package dev.joaq.ancestralpowers;

import dev.joaq.ancestralpowers.client.ModKeyBinds;
import dev.joaq.ancestralpowers.client.StaminaHudOverlay;
import dev.joaq.ancestralpowers.client.DoubleJumpHandler;
import dev.joaq.ancestralpowers.networking.packet.c2s.ToggleGPayload;
import dev.joaq.ancestralpowers.networking.packet.c2s.ToggleRPayload;
import dev.joaq.ancestralpowers.networking.packet.c2s.DoubleJumpPayload;
import dev.joaq.ancestralpowers.offhand.OffhandMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;


@Environment(EnvType.CLIENT)
public class AncestralPowersClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        OffhandMod.initClient();
        ModKeyBinds.registerKeyBinds();
        StaminaHudOverlay.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            if (ModKeyBinds.G_KEY.wasPressed()) {
                PacketByteBuf buf = net.fabricmc.fabric.api.networking.v1.PacketByteBufs.create();
                ToggleGPayload.write(buf, true);
                ClientPlayNetworking.send(ToggleGPayload.ID, buf);
            }

            if (ModKeyBinds.R_KEY.wasPressed()) {
                PacketByteBuf buf = net.fabricmc.fabric.api.networking.v1.PacketByteBufs.create();
                ToggleRPayload.write(buf, true);
                ClientPlayNetworking.send(ToggleRPayload.ID, buf);
            }

            DoubleJumpHandler.onClientTick(client);
        });

        HudRenderCallback.EVENT.register((context, tickCounter) -> {
             if (MinecraftClient.getInstance() != null) {
                 StaminaHudOverlay.render(context, MinecraftClient.getInstance().inGameHud, 1.0f);
             }
         });
    }

}
