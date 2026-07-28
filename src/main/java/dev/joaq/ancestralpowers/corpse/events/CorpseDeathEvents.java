package dev.joaq.ancestralpowers.corpse.events;

import dev.joaq.ancestralpowers.corpse.CorpseConfig;
import dev.joaq.ancestralpowers.corpse.Death;
import dev.joaq.ancestralpowers.corpse.DeathManager;
import dev.joaq.ancestralpowers.corpse.entity.CorpseEntity;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class CorpseDeathEvents {

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
            if (entity instanceof ServerPlayerEntity player) {
                if (!player.isCreative()) {
                    Death death = DeathManager.createDeathFromPlayer(player);
                    player.getWorld().spawnEntity(CorpseEntity.createFromDeath(player, death));
                    // Clear inventory so no vanilla drops happen
                    player.getInventory().clear();
                }
            }
            return true;
        });
    }
}