package dev.joaq.ancestralpowers.corpse.client.renderer;

import com.mojang.authlib.GameProfile;
import dev.joaq.ancestralpowers.corpse.CorpseConfig;
import dev.joaq.ancestralpowers.corpse.entity.CorpseEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import java.util.UUID;

public class CorpseRenderer extends EntityRenderer<CorpseEntity> {

    private static final Identifier DEFAULT_SKIN = Identifier.of("minecraft", "textures/entity/steve.png");
    private final MinecraftClient client;
    private final EntityRenderDispatcher renderDispatcher;

    public CorpseRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.client = MinecraftClient.getInstance();
        this.renderDispatcher = client.getEntityRenderDispatcher();
        this.shadowRadius = 0.5F;
    }

    @Override
    public Identifier getTexture(CorpseEntity entity) {
        return DEFAULT_SKIN;
    }

    @Override
    public void render(CorpseEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack,
                       VertexConsumerProvider buffer, int packedLightIn) {
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLightIn);
        matrixStack.push();

        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-entity.getYaw()));

        if (CorpseConfig.spawnCorpseOnFace) {
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90F));
            matrixStack.translate(0D, -1D, -2.01D / 16D);
        } else {
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90F));
            matrixStack.translate(0D, -1D, 2.01D / 16D);
        }

        UUID corpseUUID = entity.getCorpseUUID().orElse(null);
        String corpseName = entity.getCorpseName().isEmpty() ? entity.getDeath().getPlayerName() : entity.getCorpseName();

        GameProfile profile = new GameProfile(corpseUUID != null ? corpseUUID : UUID.randomUUID(), corpseName != null ? corpseName : "");

        DummyPlayer dummyPlayer = new DummyPlayer(
                (net.minecraft.client.world.ClientWorld) entity.getWorld(),
                profile,
                entity.getEquipment(),
                entity.getCorpseModel()
        );

        dummyPlayer.setYaw(0);
        dummyPlayer.setPitch(0);
        dummyPlayer.bodyYaw = 0;
        dummyPlayer.headYaw = 0;

        EntityRenderer<? super DummyPlayer> playerRenderer = renderDispatcher.getRenderer(dummyPlayer);
        playerRenderer.render(dummyPlayer, 0F, 1F, matrixStack, buffer, packedLightIn);

        matrixStack.pop();
    }
}