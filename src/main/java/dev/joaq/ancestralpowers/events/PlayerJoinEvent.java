package dev.joaq.ancestralpowers.events;

import dev.joaq.ancestralpowers.components.MyComponents;
import dev.joaq.ancestralpowers.components.PlayerTraits;
import dev.joaq.ancestralpowers.util.RandomUtils;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class PlayerJoinEvent {

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;
            PlayerTraits traits = MyComponents.TRAITS.get(player);


            if(traits.getMainPower() == null) {
                traits.setMainPower(RandomUtils.randomMain());
            }
            if(traits.getMovementPower() == null) {
                traits.setMovementPower(RandomUtils.randomMovement(traits.getMainPower()));
            }

            if(traits.getIntelligence() == null) {
                traits.setIntelligence(RandomUtils.randomIntelligence());
            }
            if(traits.getStamina() == null) {
                traits.setStamina(100f);
            }
            if(traits.getActPower_main() == null) {
                traits.setActPower_main(false);
            }
            if(traits.getActPower_secondary() == null) {
                traits.setActPower_secondary(false);
            }

//            player.sendMessage(Text.literal(
//                    "Seus poderes: " + traits.getMovementPower() + " | " +
//                            traits.getMainPower() + " | " + traits.getIntelligence() + "|" + traits.getStamina()
//            ), false);
        });


    }

}