package dev.joaq.ancestralpowers.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.client.item.TooltipContext;
import dev.joaq.ancestralpowers.util.DayNightDamageUtils;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import com.google.common.collect.Multimap;

import java.util.List;
import java.util.UUID;

public class SolarAxeItem extends AxeItem {
    private static final UUID BASE_DAMAGE_UUID = UUID.fromString("cb3f55d3-645c-4f38-a497-9c13a33db5cf");
    private static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("fa233e1c-4180-4865-b01b-bcce9b12cb10");

    public SolarAxeItem(ToolMaterial material, float attackDamage, float attackSpeed, Item.Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
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
        
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Relíquia Solar").formatted(Formatting.GOLD, Formatting.BOLD));
        tooltip.add(Text.literal("Forjada no coração do zênite, onde até as sombras se ajoelham.").formatted(Formatting.YELLOW));
        tooltip.add(Text.literal("Seu gume arde com fúria crescente conforme o sol ascende.").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Alcança seu ápice ao meio-dia e quase silencia na meia-noite.").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Escala de dano: 0.30x → 2.00x").formatted(Formatting.DARK_GRAY));
    }

    @Override
    public com.google.common.collect.Multimap<net.minecraft.entity.attribute.EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        com.google.common.collect.Multimap<net.minecraft.entity.attribute.EntityAttribute, EntityAttributeModifier> multimap = super.getAttributeModifiers(slot);
        
        if (slot == EquipmentSlot.MAINHAND) {
            multimap.put(
                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                new EntityAttributeModifier(
                    BASE_DAMAGE_UUID,
                    "Solar Axe Base Damage",
                    4.0,
                    EntityAttributeModifier.Operation.ADDITION
                )
            );
            multimap.put(
                EntityAttributes.GENERIC_ATTACK_SPEED,
                new EntityAttributeModifier(
                    BASE_ATTACK_SPEED_UUID,
                    "Solar Axe Attack Speed",
                    -2.5,
                    EntityAttributeModifier.Operation.ADDITION
                )
            );
        }
        
        return multimap;
    }
}
