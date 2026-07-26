package dev.joaq.ancestralpowers.powers.main;

import dev.joaq.ancestralpowers.components.MyComponents;
import dev.joaq.ancestralpowers.components.PlayerTraits;
import dev.joaq.ancestralpowers.powers.PowerBase;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SolarAccelerationPower extends PowerBase {

    private static final long TARGET_TIME = 6000;
    static final int SPEED_MAX = 140;

    private static final Map<UUID, AccelState> states = new ConcurrentHashMap<>();
    private static boolean tickRegistered;

    private static void ensureTickRegistered() {
        if (tickRegistered) return;
        tickRegistered = true;
        ServerTickEvents.START_WORLD_TICK.register(world -> {
            if (!(world instanceof ServerWorld sw)) return;
            tickAcceleration(sw);
        });
    }

    @Override
    protected float staminaCost() {
        return 0.5f;
    }

    @Override
    protected String ActivationType() {
        return "TOGGLE";
    }

    @Override
    protected void disablePowerSpecific(ServerPlayerEntity player) {
        deactivate(player);
    }

    @Override
    protected void executeLogic(ServerPlayerEntity player, boolean activate, float stamina) {
    }

    @Override
    public void apply(ServerPlayerEntity player, boolean activate, float stamina) {
        PlayerTraits traits = MyComponents.TRAITS.get(player);
        ensureTickRegistered();

        if (!activate) {
            deactivate(player);
            return;
        }

        ServerWorld world = (ServerWorld) player.getWorld();
        long currentTime = world.getTimeOfDay();

        if (states.containsKey(player.getUuid())) {
            var state = states.get(player.getUuid());
            if (state != null && state.frozen) {
                spendStamina(traits, staminaCost());
                if (stamina < staminaCost()) {
                    deactivate(player);
                }
                return;
            }
        }

        if (stamina < staminaCost()) {
            deactivate(player);
            player.sendMessage(Text.literal("☀ Sua stamina se esgotou. O tempo solar foi desativado."), false);
            return;
        }

        long currentNormalized = currentTime % 24000;
        if (currentNormalized == TARGET_TIME) {
            states.put(player.getUuid(), new AccelState(true));
            world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(false, player.getServer());
            player.sendMessage(Text.literal("☀ O sol atingiu o zênite. Drenando stamina..."), false);
            spendStamina(traits, staminaCost());
            return;
        }

        if (!world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
            world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(true, player.getServer());
        }

        long distance = TARGET_TIME - currentNormalized;
        if (distance <= 0) distance += 24000;
        states.put(player.getUuid(), new AccelState(currentTime, distance));

        spendStamina(traits, staminaCost());
    }

    private void deactivate(ServerPlayerEntity player) {
        states.remove(player.getUuid());
        restoreDaylightCycle(player);
        PlayerTraits traits = MyComponents.TRAITS.get(player);
        traits.setActPower_main(false);
    }

    private static void tickAcceleration(ServerWorld world) {
        MinecraftServer server = world.getServer();
        if (server == null) return;

        for (var player : server.getPlayerManager().getPlayerList()) {
            if (!player.getWorld().equals(world)) continue;
            AccelState state = states.get(player.getUuid());
            if (state == null || state.frozen) continue;

            long now = world.getTimeOfDay();
            long traveled = now - state.startTime;
            if (traveled < 0) traveled += 24000;

            long remaining = state.totalDistance - traveled;
            if (remaining <= 0) {
                long targetAbs = (state.startTime / 24000) * 24000 + TARGET_TIME;
                if (state.startTime % 24000 > TARGET_TIME) targetAbs += 24000;
                world.setTimeOfDay(targetAbs);
                states.put(player.getUuid(), new AccelState(true));
                world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(false, player.getServer());
                player.sendMessage(Text.literal("☀ O sol atingiu o zênite. Drenando stamina..."), false);
                continue;
            }

            double ratio = (double) traveled / state.totalDistance;
            int add = (int) (SPEED_MAX * sinEasing(ratio));
            if (add < 1) add = 1;
            if (add > remaining) add = (int) remaining;

            world.setTimeOfDay(now + add);
        }
    }

    private static double sinEasing(double t) {
        return Math.sin(t * Math.PI);
    }

    private static void restoreDaylightCycle(ServerPlayerEntity player) {
        ServerWorld world = (ServerWorld) player.getWorld();
        if (!world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
            world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(true, player.getServer());
            player.sendMessage(Text.literal("☀ O ciclo dia/noite foi restaurado."), false);
        }
    }

    @Override
    public void reset(ServerPlayerEntity player) {
        states.remove(player.getUuid());
        restoreDaylightCycle(player);
    }

    private static class AccelState {
        final long startTime;
        final long totalDistance;
        boolean frozen;

        AccelState(long startTime, long totalDistance) {
            this.startTime = startTime;
            this.totalDistance = totalDistance;
        }

        AccelState(boolean frozen) {
            this.startTime = 0;
            this.totalDistance = 0;
            this.frozen = frozen;
        }
    }
}
