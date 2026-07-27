package dev.joaq.ancestralpowers.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class DashBootsItem extends Item {
    public DashBootsItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Botas do Impulso Etéreo").formatted(Formatting.GOLD, Formatting.BOLD));
        tooltip.add(Text.literal("Tecidas com plumas de relâmpago e trovão engarrafado.").formatted(Formatting.YELLOW));
        tooltip.add(Text.literal("Permitem um segundo salto que te lança furiosamente para frente.").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Libera faíscas ao disparar no ar.").formatted(Formatting.GRAY));
    }
}
