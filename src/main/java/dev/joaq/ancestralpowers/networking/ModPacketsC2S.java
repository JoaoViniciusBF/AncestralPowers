package dev.joaq.ancestralpowers.networking;

import dev.joaq.ancestralpowers.components.MyComponents;
import dev.joaq.ancestralpowers.components.PlayerTraits;
import dev.joaq.ancestralpowers.networking.packet.c2s.DoubleJumpPayload;
import dev.joaq.ancestralpowers.networking.packet.c2s.OffhandAttackC2SPayload;
import dev.joaq.ancestralpowers.networking.packet.c2s.ToggleGPayload;
import dev.joaq.ancestralpowers.networking.packet.c2s.ToggleRPayload;
import dev.joaq.ancestralpowers.offhand.OffhandMod;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModPacketsC2S {

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(ToggleRPayload.PAYLOAD_ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            player.getServer().execute(() -> {
                PlayerTraits traits = MyComponents.TRAITS.get(player);
                traits.setActPower_main(!traits.getActPower_main());
                player.sendMessage(Text.literal("R = " + traits.getActPower_main()), false);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(ToggleGPayload.PAYLOAD_ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            player.getServer().execute(() -> {
                PlayerTraits traits = MyComponents.TRAITS.get(player);
                traits.setActPower_secondary(!traits.getActPower_secondary());
                player.sendMessage(Text.literal("G = " + traits.getActPower_secondary()), false);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(DoubleJumpPayload.PAYLOAD_ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            player.getServer().execute(() -> {
                player.fallDistance = 0;

                net.minecraft.server.world.ServerWorld world = (net.minecraft.server.world.ServerWorld) player.getWorld();
                world.spawnParticles(
                    ParticleTypes.ELECTRIC_SPARK,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    20, 0.5, 0.1, 0.5, 0.04
                );
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(OffhandAttackC2SPayload.ID, (payload, context) -> {
            OffhandMod.setOverride(context.player(), true);
        });
    }
}
