package dev.joaq.ancestralpowers.item;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Consumer;

public class EffectDaggerItem extends Item {
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
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        EffectPair pair = EFFECT_PAIRS.get(attacker.getRandom().nextInt(EFFECT_PAIRS.size()));
        RegistryEntry<StatusEffect> effect = inverted ? pair.positive() : pair.negative();
        target.addStatusEffect(new StatusEffectInstance(effect, 160, 0), attacker);
        super.postHit(stack, target, attacker);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        if (inverted) {
            textConsumer.accept(Text.literal("Lâmina de Inversão").formatted(titleColor, Formatting.BOLD));
            textConsumer.accept(Text.literal("Transforma maldições em bênçãos temporárias no alvo.").formatted(loreColor));
            textConsumer.accept(Text.literal("Criada para preparar presas ricas em efeitos positivos.").formatted(Formatting.GRAY));
        } else {
            textConsumer.accept(Text.literal("Lâmina de Aflição").formatted(titleColor, Formatting.BOLD));
            textConsumer.accept(Text.literal("Injeta uma maldição aleatória em quem recebe o corte.").formatted(loreColor));
            textConsumer.accept(Text.literal("Cada aflição possui uma bênção espelhada na adaga inversa.").formatted(Formatting.GRAY));
        }
        textConsumer.accept(Text.literal("Pares: Veneno/Regeneração, Lentidão/Velocidade, Fraqueza/Força").formatted(Formatting.DARK_GRAY));
        textConsumer.accept(Text.literal("Fadiga/Pressa, Cegueira/Visão Noturna, Fome/Saturação, Wither/Absorção").formatted(Formatting.DARK_GRAY));
    }

    public static AttributeModifiersComponent createAttributeModifiers(String path, float baseAttackDamage, float attackSpeed) {
        return AttributeModifiersComponent.builder()
            .add(
                EntityAttributes.ATTACK_DAMAGE,
                new EntityAttributeModifier(
                    Identifier.of("ancestralpowers", path + "_base_damage"),
                    baseAttackDamage,
                    EntityAttributeModifier.Operation.ADD_VALUE
                ),
                AttributeModifierSlot.MAINHAND
            )
            .add(
                EntityAttributes.ATTACK_SPEED,
                new EntityAttributeModifier(
                    Identifier.of("ancestralpowers", path + "_attack_speed"),
                    attackSpeed,
                    EntityAttributeModifier.Operation.ADD_VALUE
                ),
                AttributeModifierSlot.MAINHAND
            )
            .build();
    }

    private record EffectPair(RegistryEntry<StatusEffect> negative, RegistryEntry<StatusEffect> positive) {
    }
}
