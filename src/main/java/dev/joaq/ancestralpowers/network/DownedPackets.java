package dev.joaq.ancestralpowers.network;

import dev.joaq.ancestralpowers.AncestralPowers;
import dev.joaq.ancestralpowers.components.MyComponents;
import dev.joaq.ancestralpowers.components.PlayerTraits;
import dev.joaq.ancestralpowers.networking.packet.c2s.ReviveStartC2S;
import dev.joaq.ancestralpowers.networking.packet.c2s.ReviveCancelC2S;
import dev.joaq.ancestralpowers.networking.packet.s2c.DownedStateSyncS2C;
import dev.joaq.ancestralpowers.networking.packet.s2c.ReviveProgressS2C;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class DownedPackets {

    private static MinecraftServer server;

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(ReviveStartC2S.ID, (srv, player, handler, buf, sender) -> {
            ReviveStartC2S payload = ReviveStartC2S.read(buf);
            server = srv;
            srv.execute(() -> handleReviveStart(player, payload));
        });

        ServerPlayNetworking.registerGlobalReceiver(ReviveCancelC2S.ID, (srv, player, handler, buf, sender) -> {
            server = srv;
            srv.execute(() -> handleReviveCancel(player));
        });
    }

    private static void handleReviveStart(ServerPlayerEntity reviver, ReviveStartC2S payload) {
        ServerPlayerEntity target = (ServerPlayerEntity) reviver.getWorld().getEntityById(payload.targetEntityId());
        
        if (target == null || !target.isAlive()) {
            reviver.sendMessage(Text.literal("§cJogador não encontrado ou já morreu.").formatted(Formatting.RED), true);
            return;
        }
        
        PlayerTraits targetTraits = MyComponents.TRAITS.get(target);
        
        if (!targetTraits.getIsDowned()) {
            reviver.sendMessage(Text.literal("§cEste jogador não está caído.").formatted(Formatting.RED), true);
            return;
        }
        
        if (reviver.distanceTo(target) > 3.0) {
            reviver.sendMessage(Text.literal("§cMuito longe para resgatar!").formatted(Formatting.RED), true);
            return;
        }
        
        targetTraits.setReviverUuid(reviver.getUuid());
        targetTraits.setReviveProgress(0);
        
        reviver.sendMessage(Text.literal("§aIniciando resgate de §6" + target.getName().getString() + "§a... (5s)").formatted(Formatting.GREEN), true);
        target.sendMessage(Text.literal("§a§l" + reviver.getName().getString() + " está te resgatando!").formatted(Formatting.GREEN), true);
        
        ReviveProgressS2C.sendToTracking(target, 0f, true, reviver.getName().getString());
        ReviveProgressS2C.sendToTracking(reviver, 0f, true, target.getName().getString());
        
        new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                
                PlayerTraits currentTraits = MyComponents.TRAITS.get(target);
                
                if (!currentTraits.getIsDowned() || !currentTraits.getReviverUuid().equals(reviver.getUuid())) {
                    ReviveProgressS2C.sendToTracking(target, 0f, false, "");
                    ReviveProgressS2C.sendToTracking(reviver, 0f, false, "");
                    return;
                }
                
                if (reviver.distanceTo(target) > 3.5) {
                    currentTraits.clearReviverUuid();
                    currentTraits.setReviveProgress(0);
                    reviver.sendMessage(Text.literal("§cResgate cancelado: você se afastou!").formatted(Formatting.RED), true);
                    target.sendMessage(Text.literal("§cResgate cancelado: o resgatador se afastou!").formatted(Formatting.RED), true);
                    ReviveProgressS2C.sendToTracking(target, 0f, false, "");
                    ReviveProgressS2C.sendToTracking(reviver, 0f, false, "");
                    return;
                }
                
                float progress = i / 5f;
                currentTraits.setReviveProgress((int)(progress * 100));
                
                ReviveProgressS2C.sendToTracking(target, progress, true, reviver.getName().getString());
                ReviveProgressS2C.sendToTracking(reviver, progress, true, target.getName().getString());
            }
            
            server.execute(() -> completeRevive(reviver, target));
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
                ReviveProgressS2C.sendToTracking(player, 0f, false, "");
                ReviveProgressS2C.sendToTracking(reviver, 0f, false, "");
            }
        }
    }

    private static void completeRevive(ServerPlayerEntity reviver, ServerPlayerEntity target) {
        PlayerTraits targetTraits = MyComponents.TRAITS.get(target);
        
        if (!targetTraits.getIsDowned() || !targetTraits.getReviverUuid().equals(reviver.getUuid())) {
            return;
        }
        
        targetTraits.setIsDowned(false);
        targetTraits.setBleedoutTimer(0);
        targetTraits.clearReviverUuid();
        targetTraits.setReviveProgress(0);
        
        target.setHealth(4.0f);
        target.setPose(EntityPose.STANDING);
        target.clearStatusEffects();
        
        reviver.sendMessage(Text.literal("§a§lResgate concluído! §r§a" + target.getName().getString() + " foi revivido com 2 corações.").formatted(Formatting.GREEN), true);
        target.sendMessage(Text.literal("§a§lVOCÊ FOI REVIVIDO! §r§aVocê tem 2 corações de vida.").formatted(Formatting.GREEN), true);
        
        ServerWorld world = target.getServerWorld();
        world.getPlayers().forEach(p -> {
            if (p != target && p != reviver) {
                p.sendMessage(Text.literal("§a" + reviver.getName().getString() + " §eresgatou §a" + target.getName().getString() + "§e!").formatted(Formatting.GREEN), false);
            }
        });
        
        DownedStateSyncS2C.sendToTracking(target, false, 0);
        ReviveProgressS2C.sendToTracking(target, 0f, false, "");
        ReviveProgressS2C.sendToTracking(reviver, 0f, false, "");
    }

    public static void sendDownedStateSync(ServerPlayerEntity player, boolean isDowned, int bleedoutTime) {
        DownedStateSyncS2C.sendToTracking(player, isDowned, bleedoutTime);
    }
}