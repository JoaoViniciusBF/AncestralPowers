package dev.joaq.ancestralpowers.client.renderer;

import dev.joaq.ancestralpowers.entity.SummonedGolemEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SummonedGolemRenderer extends GeoEntityRenderer<SummonedGolemEntity> {

    private static final Identifier TEXTURE = new Identifier("ancestralpowers", "textures/entity/summoned_golem.png");

    public SummonedGolemRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new SummonedGolemModel());
    }

    @Override
    public Identifier getTexture(SummonedGolemEntity entity) {
        return TEXTURE;
    }
}