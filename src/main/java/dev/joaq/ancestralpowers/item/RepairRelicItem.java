package dev.joaq.ancestralpowers.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class RepairRelicItem extends Item {
    private static final int REPAIR_INTERVAL = 100;
    private static final int REPAIR_AMOUNT = 1;

    public RepairRelicItem(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!(entity instanceof PlayerEntity player) || world.getTime() % REPAIR_INTERVAL != 0) {
            return;
        }

        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack inventoryStack = player.getInventory().getStack(i);

            if (inventoryStack == stack || !inventoryStack.isDamageable() || inventoryStack.getDamage() <= 0) {
                continue;
            }

            inventoryStack.setDamage(Math.max(0, inventoryStack.getDamage() - REPAIR_AMOUNT));
            break;
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Relíquia da Restauração").formatted(Formatting.GREEN, Formatting.BOLD));
        tooltip.add(Text.literal("Um talismã antigo que murmura runas de renovação.").formatted(Formatting.DARK_GREEN));
        tooltip.add(Text.literal("Enquanto repousa no inventário, repara lentamente seus equipamentos.").formatted(Formatting.GREEN));
        tooltip.add(Text.literal("Restaura 1 ponto de durabilidade a cada 5 segundos.").formatted(Formatting.GRAY));
    }
}
