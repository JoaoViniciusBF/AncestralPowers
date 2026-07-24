package dev.joaq.ancestralpowers.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class TimeCheckerItem extends Item {
    public TimeCheckerItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient) {
            long timeOfDay = world.getTimeOfDay() % 24000;
            long hours = (timeOfDay / 1000 + 6) % 24;
            long minutes = (timeOfDay % 1000) * 60 / 1000;
            
            player.sendMessage(Text.literal(String.format("Horário do jogo: %02d:%02d (Ticks: %d)", hours, minutes, timeOfDay)), false);
        }
        
        return ActionResult.SUCCESS;
    }
}
