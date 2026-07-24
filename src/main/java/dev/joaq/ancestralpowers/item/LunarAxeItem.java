package dev.joaq.ancestralpowers.item;

import dev.joaq.ancestralpowers.util.DayNightDamageUtils;
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
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class LunarAxeItem extends AxeItem {
    private static final Identifier BASE_DAMAGE_ID = Identifier.of("ancestralpowers", "lunar_axe_base_damage");
    private static final Identifier BASE_ATTACK_SPEED_ID = Identifier.of("ancestralpowers", "lunar_axe_attack_speed");

    public LunarAxeItem(ToolMaterial material, float attackDamage, float attackSpeed, Item.Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity player) {
            World world = attacker.getWorld();
            long timeOfDay = world.getTimeOfDay() % 24000;
            
            float damageMultiplier = DayNightDamageUtils.getLunarMultiplier(world);
            
            player.sendMessage(
                Text.literal(String.format("☾ Multiplicador Lunar: %.2fx (Hora: %d ticks)", 
                    damageMultiplier, timeOfDay))
                .formatted(Formatting.AQUA), 
                true
            );
        }
        
        super.postHit(stack, target, attacker);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.literal("Relíquia Lunar").formatted(Formatting.AQUA, Formatting.BOLD));
        textConsumer.accept(Text.literal("Talhada sob um eclipse antigo, banhada por silêncio estelar.").formatted(Formatting.BLUE));
        textConsumer.accept(Text.literal("Seu corte floresce quando a noite cobre o mundo.").formatted(Formatting.AQUA));
        textConsumer.accept(Text.literal("Alcança seu ápice na meia-noite e enfraquece ao meio-dia.").formatted(Formatting.GRAY));
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
