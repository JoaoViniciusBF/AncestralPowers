package dev.joaq.ancestralpowers.offhand;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class OffhandMod {
    public static final Map<UUID, Boolean> DO_OVERRIDE = new ConcurrentHashMap<>();
    public static final Map<UUID, Integer> OFFHAND_COOLDOWN = new ConcurrentHashMap<>();

    public static void init() {
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (var player : server.getPlayerManager().getPlayerList()) {
                tickCooldown(player);
            }
        });
    }

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                tickCooldown(client.player);
            }
        });
    }

    private static void tickCooldown(PlayerEntity player) {
        OFFHAND_COOLDOWN.compute(player.getUuid(), (uuid, val) -> {
            if (val == null || val <= 0) return 0;
            return val - 1;
        });
    }

    public static boolean shouldOverride(PlayerEntity player) {
        return DO_OVERRIDE.getOrDefault(player.getUuid(), false);
    }

    public static void setOverride(PlayerEntity player, boolean value) {
        if (value) {
            DO_OVERRIDE.put(player.getUuid(), true);
        } else {
            DO_OVERRIDE.remove(player.getUuid());
        }
    }

    public static boolean canOffhandAttack(PlayerEntity player) {
        int cd = OFFHAND_COOLDOWN.getOrDefault(player.getUuid(), 0);
        return cd <= 0;
    }

    public static void resetOffhandCooldown(PlayerEntity player, ItemStack offhand) {
        int delay = getOffhandAttackDelay(offhand);
        OFFHAND_COOLDOWN.put(player.getUuid(), delay);
    }

    public static int getOffhandAttackDelay(ItemStack stack) {
         double speed = 4.0;
         var modifiers = stack.getAttributeModifiers(EquipmentSlot.OFFHAND);
         for (var entry : modifiers.entries()) {
             if (entry.getKey() == EntityAttributes.GENERIC_ATTACK_SPEED) {
                 if (entry.getValue().getOperation() == net.minecraft.entity.attribute.EntityAttributeModifier.Operation.ADDITION) {
                     speed += entry.getValue().getValue();
                 }
             }
         }
         
         double effectiveSpeed = speed * 0.8 + 0.8;
         int ticks = (int) Math.ceil(20.0 / Math.max(0.1, effectiveSpeed));
         return Math.max(4, ticks);
     }

    public static float getOffhandCooldownProgress(PlayerEntity player, ItemStack offhand) {
        int cd = OFFHAND_COOLDOWN.getOrDefault(player.getUuid(), 0);
        int delay = getOffhandAttackDelay(offhand);
        if (delay <= 0) return 1.0f;
        return 1.0f - (float) cd / delay;
    }
}
