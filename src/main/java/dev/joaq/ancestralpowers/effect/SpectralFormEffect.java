package dev.joaq.ancestralpowers.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class SpectralFormEffect extends StatusEffect {
    public SpectralFormEffect() {
        super(StatusEffectCategory.NEUTRAL, 0x88DDFF);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
