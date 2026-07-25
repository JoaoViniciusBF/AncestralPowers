package dev.joaq.ancestralpowers.powers.main;

import dev.joaq.ancestralpowers.components.MyComponents;
import dev.joaq.ancestralpowers.components.PlayerTraits;
import dev.joaq.ancestralpowers.powers.PowerBase;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LunarAccelerationPower extends PowerBase {

    private static final long TARGET_TIME = 18000;
    private static final int LOCK_DURATION = 1200;
    private static final int SPEED_MULTIPLIER = 3;

    private static final Map<UUID, PlayerState> states = new ConcurrentHashMap<>();
    private static boolean registered = false;

    public LunarAccelerationPower() {
        if (!registered) {
            registered = true;
            ServerTickEvents.END_SERVER_TICK.register(server -> {
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    tick(player);
                }
            });
        }
    }

    private void tick(ServerPlayerEntity player) {
        PlayerTraits traits = MyComponents.TRAITS.get(player);
        if (!"Lunar".equals(traits.getMainPower())) return;
        if (!traits.getActPower_main()) return;

        PlayerState state = states.computeIfAbsent(player.getUuid(), k -> new PlayerState());
        ServerWorld world = (ServerWorld) player.getWorld();

        long currentTime = world.getTimeOfDay() % 24000;

        if (!state.locked && currentTime != TARGET_TIME) {
            long targetAbsolute = (currentTime < TARGET_TIME)
                ? TARGET_TIME
                : TARGET_TIME + 24000;
            long currentAbsolute = world.getTimeOfDay();
            long increment = Math.min(targetAbsolute - currentAbsolute, SPEED_MULTIPLIER);

            if (currentTime + increment > TARGET_TIME) {
                increment = TARGET_TIME - currentTime;
                if (increment <= 0) increment = 24000 - currentTime + TARGET_TIME;
            }

            world.setTimeOfDay(currentAbsolute + increment);
        } else if (!state.locked && currentTime == TARGET_TIME) {
            state.locked = true;
            state.lockTimer = LOCK_DURATION;
            state.gameruleValue = world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE);
            world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(false, null);
            player.sendMessage(Text.literal("☽ A noite se manteve em seu apice por 1 minuto."), false);
        } else if (state.locked) {
            if (state.lockTimer > 0) {
                state.lockTimer--;
            } else {
                state.locked = false;
                world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(state.gameruleValue, null);
                states.remove(player.getUuid());
                traits.setActPower_main(false);
                player.sendMessage(Text.literal("☽ O tempo lunar termina, o ciclo dia/noite foi restaurado."), false);
            }
        }
    }

    @Override
    protected float staminaCost() {
        return 10f;
    }

    @Override
    protected String ActivationType() {
        return "TOGGLE";
    }

    @Override
    protected void disablePowerSpecific(ServerPlayerEntity player) {
        PlayerState state = states.get(player.getUuid());
        if (state != null && state.locked) {
            ServerWorld world = (ServerWorld) player.getWorld();
            world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(state.gameruleValue, null);
        }
        states.remove(player.getUuid());
        PlayerTraits traits = MyComponents.TRAITS.get(player);
        traits.setActPower_main(false);
    }

    @Override
    protected void executeLogic(ServerPlayerEntity player, boolean activate, float stamina) {
        ServerWorld world = (ServerWorld) player.getWorld();
        long currentTime = world.getTimeOfDay() % 24000;

        if (currentTime == TARGET_TIME) {
            player.sendMessage(Text.literal("☽ Já é meia-noite! A lua será mantida por 1 minuto."), false);
            return;
        }

        player.sendMessage(Text.literal("☽ Acelerando o tempo até a meia-noite... (3x)"), false);
    }

    @Override
    public void apply(ServerPlayerEntity player, boolean activate, float stamina) {
        PlayerTraits traits = MyComponents.TRAITS.get(player);

        if (!activate) {
            disablePower(traits, "Specific", player);
            return;
        }

        boolean alreadyActive = states.containsKey(player.getUuid());
        if (!alreadyActive) {
            executeLogic(player, true, stamina);
            spendStamina(traits, staminaCost());
        }
    }

    @Override
    public void reset(ServerPlayerEntity player) {
        states.remove(player.getUuid());
    }

    private static class PlayerState {
        boolean locked;
        int lockTimer;
        boolean gameruleValue;
    }
}
