package dev.joaq.ancestralpowers.item;

import dev.joaq.ancestralpowers.util.DayNightDamageUtils;
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
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import java.util.List;
import java.util.UUID;

public class LunarAxeItem extends AxeItem {
    private static final UUID BASE_DAMAGE_UUID = UUID.fromString("5c7f3e8a-1b9c-4d2e-a5f1-9e3c7b2d1a0f");
    private static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("3a9e2b1c-5f8d-4a7e-9b3c-1d5f8e2a9c3b");

    public LunarAxeItem(ToolMaterial material, float attackDamage, float attackSpeed, Item.Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
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
        
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Relíquia Lunar").formatted(Formatting.AQUA, Formatting.BOLD));
        tooltip.add(Text.literal("Talhada sob um eclipse antigo, banhada por silêncio estelar.").formatted(Formatting.BLUE));
        tooltip.add(Text.literal("Seu corte floresce quando a noite cobre o mundo.").formatted(Formatting.AQUA));
        tooltip.add(Text.literal("Alcança seu ápice na meia-noite e enfraquece ao meio-dia.").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Escala de dano: 0.30x → 2.00x").formatted(Formatting.DARK_GRAY));
    }

    @Override
    public com.google.common.collect.Multimap<net.minecraft.entity.attribute.EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        ImmutableMultimap.Builder<net.minecraft.entity.attribute.EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(super.getAttributeModifiers(slot));
        
        if (slot == EquipmentSlot.MAINHAND) {
            builder.put(
                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                new EntityAttributeModifier(
                    BASE_DAMAGE_UUID,
                    "Lunar Axe Base Damage",
                    4.0,
                    EntityAttributeModifier.Operation.ADDITION
                )
            );
            builder.put(
                EntityAttributes.GENERIC_ATTACK_SPEED,
                new EntityAttributeModifier(
                    BASE_ATTACK_SPEED_UUID,
                    "Lunar Axe Attack Speed",
                    -2.4,
                    EntityAttributeModifier.Operation.ADDITION
                )
            );
        }
        
        return builder.build();
    }
}
