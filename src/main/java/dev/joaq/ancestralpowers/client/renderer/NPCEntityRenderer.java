package dev.joaq.ancestralpowers.client.renderer;

import com.mojang.authlib.GameProfile;
import dev.joaq.ancestralpowers.npc.NPCEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Identifier;

public class NPCEntityRenderer extends LivingEntityRenderer<NPCEntity, PlayerEntityModel<NPCEntity>> {

    private static final Identifier DEFAULT_SKIN = new Identifier("textures/entity/steve.png");

    @SuppressWarnings("unchecked")
    public NPCEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new PlayerEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER), false), 0.5f);
        this.addFeature(new HeldItemFeatureRenderer(this, ctx.getHeldItemRenderer()));
        this.addFeature(new ArmorFeatureRenderer(this,
                (ArmorEntityModel) new ArmorEntityModel(ctx.getPart(EntityModelLayers.PLAYER_INNER_ARMOR)),
                (ArmorEntityModel) new ArmorEntityModel(ctx.getPart(EntityModelLayers.PLAYER_OUTER_ARMOR)),
                ctx.getModelManager()));
    }

    @Override
    public Identifier getTexture(NPCEntity entity) {
        MinecraftClient client = MinecraftClient.getInstance();
        try {
            GameProfile profile = entity.getGameProfile();
            if (profile != null && profile.getProperties().containsKey("textures")) {
                return client.getSkinProvider().getSkinTextures(profile).texture();
            }
        } catch (Exception e) {
        }
        if (client.player != null) {
            return client.player.getSkinTextures().texture();
        }
        return DEFAULT_SKIN;
    }
}
