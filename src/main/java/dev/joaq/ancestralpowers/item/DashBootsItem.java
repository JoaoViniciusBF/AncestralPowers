package dev.joaq.ancestralpowers.item;

import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;

public class DashBootsItem extends Item {
    public DashBootsItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.literal("Botas do Impulso Etéreo").formatted(Formatting.GOLD, Formatting.BOLD));
        textConsumer.accept(Text.literal("Tecidas com plumas de relâmpago e trovão engarrafado.").formatted(Formatting.YELLOW));
        textConsumer.accept(Text.literal("Permitem um segundo salto que te lança furiosamente para frente.").formatted(Formatting.GOLD));
        textConsumer.accept(Text.literal("Libera faíscas ao disparar no ar.").formatted(Formatting.GRAY));
    }
}
