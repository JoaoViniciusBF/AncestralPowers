package dev.joaq.ancestralpowers.item;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import dev.joaq.ancestralpowers.util.DayNightDamageUtils;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class SolarAxeItem extends AxeItem {
    private static final Identifier BASE_DAMAGE_ID = Identifier.of("ancestralpowers", "solar_axe_base_damage");
    private static final Identifier BASE_ATTACK_SPEED_ID = Identifier.of("ancestralpowers", "solar_axe_attack_speed");

    public SolarAxeItem(ToolMaterial material, float attackDamage, float attackSpeed, Item.Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity player) {
            World world = attacker.getWorld();
            long timeOfDay = world.getTimeOfDay() % 24000;
            
            float bonusMultiplier = DayNightDamageUtils.getSolarMultiplier(world);
            
            player.sendMessage(
                Text.literal(String.format("☀ Multiplicador Solar: %.2fx (Hora: %d ticks)", 
                    bonusMultiplier, timeOfDay))
                .formatted(Formatting.GOLD), 
                true
            );
        }
        
        super.postHit(stack, target, attacker);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.literal("Relíquia Solar").formatted(Formatting.GOLD, Formatting.BOLD));
        textConsumer.accept(Text.literal("Forjada no coração do zênite, onde até as sombras se ajoelham.").formatted(Formatting.YELLOW));
        textConsumer.accept(Text.literal("Seu gume arde com fúria crescente conforme o sol ascende.").formatted(Formatting.GOLD));
        textConsumer.accept(Text.literal("Alcança seu ápice ao meio-dia e quase silencia na meia-noite.").formatted(Formatting.GRAY));
        textConsumer.accept(Text.literal("Escala de dano: 0.30x → 2.00x").formatted(Formatting.DARK_GRAY));
    }

    public static AttributeModifiersComponent createAttributeModifiers(ToolMaterial material, float baseAttackDamage, float attackSpeed) {
        return AttributeModifiersComponent.builder()
            .add(
                EntityAttributes.ATTACK_DAMAGE,
                new EntityAttributeModifier(
                    BASE_DAMAGE_ID,
                    baseAttackDamage,
                    EntityAttributeModifier.Operation.ADD_VALUE
                ),
                AttributeModifierSlot.MAINHAND
            )
            .add(
                EntityAttributes.ATTACK_SPEED,
                new EntityAttributeModifier(
                    BASE_ATTACK_SPEED_ID,
                    attackSpeed,
                    EntityAttributeModifier.Operation.ADD_VALUE
                ),
                AttributeModifierSlot.MAINHAND
            )
            .build();
    }
}
