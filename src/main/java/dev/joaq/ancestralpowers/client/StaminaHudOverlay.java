package dev.joaq.ancestralpowers.client;

import dev.joaq.ancestralpowers.networking.packet.s2c.StaminaSyncPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class StaminaHudOverlay {
    private static final int RADIUS = 18;
    private static final int THICKNESS = 4;
    private static final int SEGMENTS = 96;
    private static final int BACKGROUND_COLOR = 0x80000000;
    private static final int EMPTY_COLOR = 0xAA202020;

    private static float clientStamina = 100f;
    private static float clientMaxStamina = 100f;
    private static boolean receivedFirstPacket = false;

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(StaminaSyncPayload.PAYLOAD_ID, (payload, context) -> {
            context.client().execute(() -> {
                clientStamina = payload.currentStamina();
                clientMaxStamina = payload.maxStamina();
                receivedFirstPacket = true;
            });
        });
    }

    public static void render(DrawContext context, InGameHud hud, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!receivedFirstPacket || client.player == null || clientMaxStamina <= 0) {
            return;
        }

        float ratio = Math.clamp(clientStamina / clientMaxStamina, 0f, 1f);
        int percentage = Math.round(ratio * 100);
        int centerX = context.getScaledWindowWidth() - 32;
        int centerY = context.getScaledWindowHeight() - 32;
        int color = staminaColor(ratio);

        drawRing(context, centerX, centerY, RADIUS + 2, RADIUS + THICKNESS + 2, 1f, BACKGROUND_COLOR);
        drawRing(context, centerX, centerY, RADIUS, RADIUS + THICKNESS, 1f, EMPTY_COLOR);
        drawRing(context, centerX, centerY, RADIUS, RADIUS + THICKNESS, ratio, color);

        String text = percentage + "%";
        int textWidth = client.textRenderer.getWidth(text);
        context.drawTextWithShadow(client.textRenderer, Text.literal(text), centerX - textWidth / 2, centerY - 4, 0xFFFFFFFF);
    }

    private static void drawRing(DrawContext context, int centerX, int centerY, int innerRadius, int outerRadius, float progress, int color) {
        int maxSegments = Math.round(SEGMENTS * progress);
        for (int i = 0; i < maxSegments; i++) {
            double startAngle = -Math.PI / 2 + (Math.PI * 2 * i / SEGMENTS);
            double endAngle = -Math.PI / 2 + (Math.PI * 2 * (i + 1) / SEGMENTS);

            int x1 = centerX + (int) (Math.cos(startAngle) * innerRadius);
            int y1 = centerY + (int) (Math.sin(startAngle) * innerRadius);
            int x2 = centerX + (int) (Math.cos(startAngle) * outerRadius);
            int y2 = centerY + (int) (Math.sin(startAngle) * outerRadius);
            int x3 = centerX + (int) (Math.cos(endAngle) * outerRadius);
            int y3 = centerY + (int) (Math.sin(endAngle) * outerRadius);
            int x4 = centerX + (int) (Math.cos(endAngle) * innerRadius);
            int y4 = centerY + (int) (Math.sin(endAngle) * innerRadius);

            context.fill(x1, y1, x2, y2, color);
            context.fill(x2, y2, x3, y3, color);
            context.fill(x3, y3, x4, y4, color);
            context.fill(x4, y4, x1, y1, color);
        }
    }

    private static int staminaColor(float ratio) {
        int red = Math.round(255 * (1f - ratio));
        int green = Math.round(255 * ratio);
        return 0xFF000000 | (red << 16) | (green << 8);
    }

    public static float getClientStamina() {
        return clientStamina;
    }

    public static float getClientMaxStamina() {
        return clientMaxStamina;
    }
}
