package dev.joaq.ancestralpowers.events;

import dev.joaq.ancestralpowers.components.MyComponents;
import dev.joaq.ancestralpowers.components.PlayerTraits;
import dev.joaq.ancestralpowers.networking.ModPacketsS2C;
import dev.joaq.ancestralpowers.powers.PowersManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerPowersTickHandler {

    private static final float MAX_STAMINA = 100f;
    private static final float STAMINA_REGEN_PER_TICK = 0.25f;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                PlayerTraits traits = MyComponents.TRAITS.get(player);

                PowersManager.applyMainPower(player, traits.getMainPower(), traits.getActPower_main(), traits.getStamina());
                PowersManager.applyMovementPower(player, traits.getMovementPower(), traits.getActPower_secondary(), traits.getStamina());

                float current = traits.getStamina();
                float newStamina = Math.min(current + STAMINA_REGEN_PER_TICK, MAX_STAMINA);
                traits.setStamina(newStamina);

                ModPacketsS2C.sendStaminaSync(player, newStamina, MAX_STAMINA);
            }
        });
    }
}
