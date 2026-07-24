package dev.joaq.ancestralpowers.item;

import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;

public class DoubleJumpBootsItem extends Item {
    public DoubleJumpBootsItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.literal("Botas do Salto Etéreo").formatted(Formatting.AQUA, Formatting.BOLD));
        textConsumer.accept(Text.literal("Costuradas com fios de vento e essência de céu profundo.").formatted(Formatting.BLUE));
        textConsumer.accept(Text.literal("Permitem um segundo salto enquanto você ainda está no ar.").formatted(Formatting.AQUA));
        textConsumer.accept(Text.literal("Ao despertar o salto, liberam partículas azuis ao redor do portador.").formatted(Formatting.GRAY));
    }
}
