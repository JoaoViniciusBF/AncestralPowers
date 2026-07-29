package dev.joaq.ancestralpowers.client;

import dev.joaq.ancestralpowers.networking.packet.s2c.DownedStateSyncS2C;
import dev.joaq.ancestralpowers.networking.packet.s2c.ReviveProgressS2C;
import dev.joaq.ancestralpowers.util.DownedStateTracker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class DownedHudOverlay {
    private static boolean clientIsDowned = false;
    private static int clientBleedoutTime = 0;
    private static float clientReviveProgress = 0f;
    private static boolean clientIsReviving = false;
    private static String clientTargetName = "";
    private static boolean receivedFirstPacket = false;

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(DownedStateSyncS2C.ID, (client, handler, buf, sender) -> {
            DownedStateSyncS2C payload = DownedStateSyncS2C.read(buf);
            client.execute(() -> {
                clientIsDowned = payload.isDowned();
                clientBleedoutTime = payload.bleedoutTimeRemaining();
                receivedFirstPacket = true;

                if (client.player != null) {
                    DownedStateTracker.setDowned(client.player.getUuid(), payload.isDowned());
                }

                if (!clientIsDowned) {
                    clientReviveProgress = 0f;
                    clientIsReviving = false;
                    clientTargetName = "";
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(ReviveProgressS2C.ID, (client, handler, buf, sender) -> {
            ReviveProgressS2C payload = ReviveProgressS2C.read(buf);
            client.execute(() -> {
                clientReviveProgress = payload.progress();
                clientIsReviving = payload.isReviving();
                clientTargetName = payload.targetName();
                
                if (!clientIsReviving) {
                    clientReviveProgress = 0f;
                    clientTargetName = "";
                }
            });
        });
    }

    public static void render(DrawContext context, InGameHud hud, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!receivedFirstPacket || client.player == null) {
            return;
        }

        if (clientIsDowned) {
            renderBleedoutTimer(context);
        }

        if (clientIsReviving) {
            renderReviveProgress(context);
        }
    }

    private static void renderBleedoutTimer(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();
        
        int secondsLeft = Math.max(0, clientBleedoutTime / 20);
        String timeText = String.format("§c§lSANGRAMENTO: §r§c%ds", secondsLeft);
        
        int textWidth = client.textRenderer.getWidth(timeText);
        int x = (width - textWidth) / 2;
        int y = height / 2 - 60;
        
        context.fill(x - 10, y - 5, x + textWidth + 10, y + 20, 0x80000000);
        context.drawTextWithShadow(client.textRenderer, Text.literal(timeText), x, y, 0xFFFFFFFF);
        
        int barWidth = 200;
        int barHeight = 10;
        int barX = (width - barWidth) / 2;
        int barY = y + 25;
        
        float progress = Math.max(0f, (float)clientBleedoutTime / 2000f);
        
        context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xAA330000);
        context.fill(barX, barY, barX + (int)(barWidth * progress), barY + barHeight, 0xAAFF0000);
        context.drawBorder(barX - 1, barY - 1, barWidth + 2, barHeight + 2, 0xFFFFFFFF);
        
        String actionText = "§eSegure §6Clique Direito §eem um aliado para ser resgatado";
        int actionWidth = client.textRenderer.getWidth(actionText);
        context.drawTextWithShadow(client.textRenderer, Text.literal(actionText), (width - actionWidth) / 2, barY + barHeight + 5, 0xFFFFFF00);
    }

    private static void renderReviveProgress(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();
        
        String actionText = clientIsDowned 
            ? String.format("§a§lRESGATANDO §6%s§a... §r§a%.0f%%", clientTargetName, clientReviveProgress * 100)
            : String.format("§a§lRESGATANDO §6%s§a... §r§a%.0f%%", clientTargetName, clientReviveProgress * 100);
        
        int textWidth = client.textRenderer.getWidth(actionText);
        int x = (width - textWidth) / 2;
        int y = height / 2 + 60;
        
        context.fill(x - 10, y - 5, x + textWidth + 10, y + 20, 0x80003300);
        context.drawTextWithShadow(client.textRenderer, Text.literal(actionText), x, y, 0xFF00FF00);
        
        int barWidth = 250;
        int barHeight = 12;
        int barX = (width - barWidth) / 2;
        int barY = y + 25;
        
        context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xAA003300);
        context.fill(barX, barY, barX + (int)(barWidth * clientReviveProgress), barY + barHeight, 0xAA00FF00);
        context.drawBorder(barX - 1, barY - 1, barWidth + 2, barHeight + 2, 0xFFFFFFFF);
        
        String releaseText = "§cSolte o Clique Direito ou afaste-se para cancelar";
        int releaseWidth = client.textRenderer.getWidth(releaseText);
        context.drawTextWithShadow(client.textRenderer, Text.literal(releaseText), (width - releaseWidth) / 2, barY + barHeight + 5, 0xFFFF0000);
    }
}