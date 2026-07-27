package dev.joaq.ancestralpowers.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import java.util.List;
import java.util.UUID;

public class EffectDaggerItem extends Item {
    private static final UUID BASE_DAMAGE_UUID = UUID.fromString("72f90b4a-8b2e-4c1f-9e3d-5a7c2b1e8d4f");
    private static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("8c3f2e9a-5d1b-4a7e-9c3e-2f8a1d5b9e4c");
    private static final List<EffectPair> EFFECT_PAIRS = List.of(
        new EffectPair(StatusEffects.POISON, StatusEffects.REGENERATION),
        new EffectPair(StatusEffects.SLOWNESS, StatusEffects.SPEED),
        new EffectPair(StatusEffects.WEAKNESS, StatusEffects.STRENGTH),
        new EffectPair(StatusEffects.MINING_FATIGUE, StatusEffects.HASTE),
        new EffectPair(StatusEffects.BLINDNESS, StatusEffects.NIGHT_VISION),
        new EffectPair(StatusEffects.HUNGER, StatusEffects.SATURATION),
        new EffectPair(StatusEffects.WITHER, StatusEffects.ABSORPTION)
    );

    private final boolean inverted;
    private final Formatting titleColor;
    private final Formatting loreColor;

    public EffectDaggerItem(ToolMaterial material, float attackDamage, float attackSpeed, Item.Settings settings, boolean inverted, Formatting titleColor, Formatting loreColor) {
        super(settings);
        this.inverted = inverted;
        this.titleColor = titleColor;
        this.loreColor = loreColor;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        EffectPair pair = EFFECT_PAIRS.get(attacker.getRandom().nextInt(EFFECT_PAIRS.size()));
        StatusEffect effect = inverted ? pair.positive() : pair.negative();
        target.addStatusEffect(new StatusEffectInstance(effect, 160, 0), attacker);
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        if (inverted) {
            tooltip.add(Text.literal("Lâmina de Inversão").formatted(titleColor, Formatting.BOLD));
            tooltip.add(Text.literal("Transforma maldições em bênçãos temporárias no alvo.").formatted(loreColor));
            tooltip.add(Text.literal("Criada para preparar presas ricas em efeitos positivos.").formatted(Formatting.GRAY));
        } else {
            tooltip.add(Text.literal("Lâmina de Aflição").formatted(titleColor, Formatting.BOLD));
            tooltip.add(Text.literal("Injeta uma maldição aleatória em quem recebe o corte.").formatted(loreColor));
            tooltip.add(Text.literal("Cada aflição possui uma bênção espelhada na adaga inversa.").formatted(Formatting.GRAY));
        }
        tooltip.add(Text.literal("Pares: Veneno/Regeneração, Lentidão/Velocidade, Fraqueza/Força").formatted(Formatting.DARK_GRAY));
        tooltip.add(Text.literal("Fadiga/Pressa, Cegueira/Visão Noturna, Fome/Saturação, Wither/Absorção").formatted(Formatting.DARK_GRAY));
    }

    @Override
    public com.google.common.collect.Multimap<net.minecraft.entity.attribute.EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        com.google.common.collect.Multimap<net.minecraft.entity.attribute.EntityAttribute, EntityAttributeModifier> multimap = super.getAttributeModifiers(slot);
        
        if (slot == EquipmentSlot.MAINHAND) {
            ImmutableMultimap.Builder<net.minecraft.entity.attribute.EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
            builder.putAll(multimap);
            
            builder.put(
                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                new EntityAttributeModifier(
                    BASE_DAMAGE_UUID,
                    "Effect Dagger Base Damage",
                    2.0,
                    EntityAttributeModifier.Operation.ADDITION
                )
            );
            builder.put(
                EntityAttributes.GENERIC_ATTACK_SPEED,
                new EntityAttributeModifier(
                    BASE_ATTACK_SPEED_UUID,
                    "Effect Dagger Attack Speed",
                    1.5,
                    EntityAttributeModifier.Operation.ADDITION
                )
            );
            
            return builder.build();
        }
        
        return multimap;
    }

    private record EffectPair(StatusEffect negative, StatusEffect positive) {
    }
}
