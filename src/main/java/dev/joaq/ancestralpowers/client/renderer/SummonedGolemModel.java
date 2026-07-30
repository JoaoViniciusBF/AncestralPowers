package dev.joaq.ancestralpowers.client.renderer;

import dev.joaq.ancestralpowers.entity.SummonedGolemEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class SummonedGolemModel extends GeoModel<SummonedGolemEntity> {
    @Override
    public Identifier getModelResource(SummonedGolemEntity entity) {
        return new Identifier("ancestralpowers", "geo/summoned_golem.geo.json");
    }

    @Override
    public Identifier getTextureResource(SummonedGolemEntity entity) {
        return new Identifier("ancestralpowers", "textures/entity/summoned_golem.png");
    }

    @Override
    public Identifier getAnimationResource(SummonedGolemEntity entity) {
        return new Identifier("ancestralpowers", "animations/summoned_golem.animation.json");
    }
}