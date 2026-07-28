package dev.joaq.ancestralpowers.registry;

import dev.joaq.ancestralpowers.effect.PowerSuppressionEffect;
import dev.joaq.ancestralpowers.effect.SpectralFormEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModEffects {
    public static final RegistryEntry<StatusEffect> POWER_SUPPRESSION = Registry.registerReference(
            Registries.STATUS_EFFECT,
            new Identifier("ancestralpowers", "power_suppression"),
            new PowerSuppressionEffect()
    );

    public static final RegistryEntry<StatusEffect> SPECTRAL_FORM = Registry.registerReference(
            Registries.STATUS_EFFECT,
            new Identifier("ancestralpowers", "spectral_form"),
            new SpectralFormEffect()
    );

    public static void register() {
        // chamado no ModInitializer
    }
}
