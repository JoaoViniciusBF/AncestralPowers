package dev.joaq.ancestralpowers.networking;

import dev.joaq.ancestralpowers.components.MyComponents;
import dev.joaq.ancestralpowers.components.PlayerTraits;
import dev.joaq.ancestralpowers.networking.ModPacketsS2C;
import dev.joaq.ancestralpowers.networking.packet.c2s.DoubleJumpPayload;
import dev.joaq.ancestralpowers.networking.packet.c2s.ReviveCancelC2S;
import dev.joaq.ancestralpowers.networking.packet.c2s.ReviveStartC2S;
import dev.joaq.ancestralpowers.networking.packet.c2s.ToggleGPayload;
import dev.joaq.ancestralpowers.networking.packet.c2s.ToggleRPayload;
import dev.joaq.ancestralpowers.util.DownedStateTracker;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityPose;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ModPacketsC2S {

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(ToggleRPayload.ID, (server, player, handler, buf, sender) -> {
            ToggleRPayload payload = ToggleRPayload.read(buf);
            server.execute(() -> {
                PlayerTraits traits = MyComponents.TRAITS.get(player);
                traits.setActPower_main(!traits.getActPower_main());
                player.sendMessage(Text.literal("R = " + traits.getActPower_main()), false);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(ToggleGPayload.ID, (server, player, handler, buf, sender) -> {
            ToggleGPayload payload = ToggleGPayload.read(buf);
            server.execute(() -> {
                PlayerTraits traits = MyComponents.TRAITS.get(player);
                traits.setActPower_secondary(!traits.getActPower_secondary());
                player.sendMessage(Text.literal("G = " + traits.getActPower_secondary()), false);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(DoubleJumpPayload.ID, (server, player, handler, buf, sender) -> {
            DoubleJumpPayload payload = DoubleJumpPayload.read(buf);
            server.execute(() -> {
                player.fallDistance = 0;

                ServerWorld world = (ServerWorld) player.getWorld();
                world.spawnParticles(
                    ParticleTypes.ELECTRIC_SPARK,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    20, 0.5, 0.1, 0.5, 0.04
                );
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(ReviveStartC2S.ID, (server, player, handler, buf, sender) -> {
            ReviveStartC2S payload = ReviveStartC2S.read(buf);
            server.execute(() -> handleReviveStart(player, payload, server));
        });

        ServerPlayNetworking.registerGlobalReceiver(ReviveCancelC2S.ID, (server, player, handler, buf, sender) -> {
            ReviveCancelC2S payload = ReviveCancelC2S.read(buf);
            server.execute(() -> handleReviveCancel(player));
        });
    }

    private static void handleReviveStart(ServerPlayerEntity reviver, ReviveStartC2S payload, net.minecraft.server.MinecraftServer server) {
        ServerPlayerEntity target = (ServerPlayerEntity) reviver.getWorld().getEntityById(payload.targetEntityId());
        
        if (target == null || !target.isAlive()) {
            reviver.sendMessage(Text.literal("§cJogador não encontrado ou já morreu."), true);
            return;
        }
        
        PlayerTraits targetTraits = MyComponents.TRAITS.get(target);
        
        if (!targetTraits.getIsDowned()) {
            reviver.sendMessage(Text.literal("§cEste jogador não está caído."), true);
            return;
        }
        
        if (targetTraits.getReviverUuid() != null && !targetTraits.getReviverUuid().equals(reviver.getUuid())) {
            reviver.sendMessage(Text.literal("§cOutro jogador já está resgatando este jogador."), true);
            return;
        }
        
        targetTraits.setReviverUuid(reviver.getUuid());
        targetTraits.setReviveProgress(0);
        
        reviver.sendMessage(Text.literal("§aIniciando resgate de §6" + target.getName().getString() + "§a... (5s)"), true);
        target.sendMessage(Text.literal("§a§l" + reviver.getName().getString() + " §r§acomeçou a te resgatar!"), true);
        
        ModPacketsS2C.sendReviveProgress(reviver, 0f, true, target.getName().getString());
        ModPacketsS2C.sendReviveProgress(target, 0f, true, reviver.getName().getString());

        new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                
                final int iteration = i;
                server.execute(() -> {
                    if (!target.isAlive()) return;
                    
                    PlayerTraits currentTraits = MyComponents.TRAITS.get(target);
                    
                    if (!currentTraits.getIsDowned() || !reviver.getUuid().equals(currentTraits.getReviverUuid())) {
                        ModPacketsS2C.sendReviveProgress(target, 0f, false, "");
                        ModPacketsS2C.sendReviveProgress(reviver, 0f, false, "");
                        return;
                    }
                    
                    if (reviver.squaredDistanceTo(target) > 16) {
                        currentTraits.clearReviverUuid();
                        currentTraits.setReviveProgress(0);
                        reviver.sendMessage(Text.literal("§cResgate cancelado: você se afastou!"), true);
                        target.sendMessage(Text.literal("§cResgate cancelado: " + reviver.getName().getString() + " se afastou!"), true);
                        ModPacketsS2C.sendReviveProgress(target, 0f, false, "");
                        ModPacketsS2C.sendReviveProgress(reviver, 0f, false, "");
                        return;
                    }
                    
                    float progress = iteration / 5f;
                    currentTraits.setReviveProgress((int)(progress * 100));
                    
                    ModPacketsS2C.sendReviveProgress(target, progress, true, reviver.getName().getString());
                    ModPacketsS2C.sendReviveProgress(reviver, progress, true, target.getName().getString());
                });
            }
            
            server.execute(() -> {
                if (target != null && target.isAlive()) {
                    PlayerTraits currentTraits = MyComponents.TRAITS.get(target);
                    if (currentTraits.getIsDowned() && reviver.getUuid().equals(currentTraits.getReviverUuid())) {
                        completeRevive(reviver, target);
                    }
                }
            });
        }).start();
    }

    private static void handleReviveCancel(ServerPlayerEntity reviver) {
        for (ServerPlayerEntity player : reviver.getServer().getPlayerManager().getPlayerList()) {
            PlayerTraits traits = MyComponents.TRAITS.get(player);
            if (traits.getIsDowned() && traits.getReviverUuid() != null && traits.getReviverUuid().equals(reviver.getUuid())) {
                traits.clearReviverUuid();
                traits.setReviveProgress(0);
                player.sendMessage(Text.literal("§cResgate cancelado por " + reviver.getName().getString()), true);
                reviver.sendMessage(Text.literal("§cResgate cancelado."), true);
                ModPacketsS2C.sendReviveProgress(player, 0f, false, "");
                ModPacketsS2C.sendReviveProgress(reviver, 0f, false, "");
            }
        }
    }

    private static void completeRevive(ServerPlayerEntity reviver, ServerPlayerEntity target) {
        if (!target.isAlive()) return;
        
        PlayerTraits targetTraits = MyComponents.TRAITS.get(target);
        
        if (!targetTraits.getIsDowned() || !reviver.getUuid().equals(targetTraits.getReviverUuid())) {
            return;
        }

        DownedStateTracker.setDowned(target.getUuid(), false);
        targetTraits.setIsDowned(false);
        targetTraits.setBleedoutTimer(0);
        targetTraits.clearReviverUuid();
        targetTraits.setReviveProgress(0);

        target.setHealth(4.0f);
        target.setPose(EntityPose.STANDING);
        target.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MOVEMENT_SPEED)
            .setBaseValue(0.1);
        target.clearStatusEffects();

        reviver.sendMessage(Text.literal("§a§lResgate concluído! §r§a" + target.getName().getString() + " foi revivido com 2 corações."), true);
        target.sendMessage(Text.literal("§a§lVOCÊ FOI REVIVIDO! §r§aVocê tem 2 corações de vida."), true);

        ServerWorld world = target.getServerWorld();
        world.getPlayers().forEach(p -> {
            if (p != target && p != reviver) {
                p.sendMessage(Text.literal("§a" + reviver.getName().getString() + " §eresgatou §a" + target.getName().getString() + "§e!"), false);
            }
        });

        ModPacketsS2C.sendDownedStateSync(target, false, 0);
        ModPacketsS2C.sendReviveProgress(target, 0f, false, "");
        ModPacketsS2C.sendReviveProgress(reviver, 0f, false, "");
    }
}