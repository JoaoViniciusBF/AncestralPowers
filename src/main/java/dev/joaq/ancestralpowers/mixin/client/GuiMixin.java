package dev.joaq.ancestralpowers.mixin.client;

import dev.joaq.ancestralpowers.offhand.OffhandMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public class GuiMixin {

    private static final Identifier OFFHAND_INDICATOR_BG = Identifier.of("hud/hotbar_attack_indicator_background");
    private static final Identifier OFFHAND_INDICATOR_FILL = Identifier.of("hud/hotbar_attack_indicator_progress");

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "renderHotbar", at = @At("TAIL"))
    private void onRenderHotbar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        PlayerEntity player = this.client.player;
        if (player == null) return;
        if (player.getOffHandStack().isEmpty()) return;
        if (!hasAttackDamage(player.getOffHandStack())) return;

        float progress = OffhandMod.getOffhandCooldownProgress(player, player.getOffHandStack());

        int hw = client.getWindow().getScaledWidth();
        int hh = client.getWindow().getScaledHeight();

        boolean rightHanded = player.getMainArm() == net.minecraft.util.Arm.RIGHT;
        int x = rightHanded ? hw / 2 + 91 + 6 + 52 : hw / 2 - 91 - 52 - 52;
        int y = hh - 20;

        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, OFFHAND_INDICATOR_BG, x, y, 18, 18, 0, 0, 18, 18);

        int fillH = (int) (progress * 18.0F);
        if (fillH > 0) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, OFFHAND_INDICATOR_FILL, x, y + 18 - fillH, 18, fillH, 0, 18 - fillH, 18, fillH);
        }
    }

    private static boolean hasAttackDamage(net.minecraft.item.ItemStack stack) {
        var mods = stack.get(net.minecraft.component.DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (mods == null) return false;
        for (var entry : mods.modifiers()) {
            if (entry.attribute().value() == net.minecraft.entity.attribute.EntityAttributes.ATTACK_DAMAGE.value())
                return true;
        }
        return false;
    }
}
