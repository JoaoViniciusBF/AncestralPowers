package dev.joaq.ancestralpowers.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class DoubleJumpBootsItem extends Item {
    public DoubleJumpBootsItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Botas do Salto Etéreo").formatted(Formatting.AQUA, Formatting.BOLD));
        tooltip.add(Text.literal("Costuradas com fios de vento e essência de céu profundo.").formatted(Formatting.BLUE));
        tooltip.add(Text.literal("Permitem um segundo salto enquanto você ainda está no ar.").formatted(Formatting.AQUA));
        tooltip.add(Text.literal("Ao despertar o salto, liberam partículas azuis ao redor do portador.").formatted(Formatting.GRAY));
    }
}
