package dev.joaq.ancestralpowers.powers.main;

import dev.joaq.ancestralpowers.components.MyComponents;
import dev.joaq.ancestralpowers.components.PlayerTraits;
import dev.joaq.ancestralpowers.powers.PowerBase;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;

public class LunarAccelerationPower extends PowerBase {

    private static final long TARGET_TIME = 18000;
    static final int SPEED_ADD = 1200;

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
        restoreDaylightCycle(player);
        PlayerTraits traits = MyComponents.TRAITS.get(player);
        traits.setActPower_main(false);
    }

    @Override
    protected void executeLogic(ServerPlayerEntity player, boolean activate, float stamina) {
    }

    @Override
    public void apply(ServerPlayerEntity player, boolean activate, float stamina) {
        PlayerTraits traits = MyComponents.TRAITS.get(player);

        if (!activate) {
            traits.setActPower_main(false);
            restoreDaylightCycle(player);
            return;
        }

        if (stamina < staminaCost()) {
            traits.setActPower_main(false);
            restoreDaylightCycle(player);
            player.sendMessage(Text.literal("☽ Sua stamina se esgotou. O tempo lunar foi desativado."), false);
            return;
        }

        ServerWorld world = (ServerWorld) player.getWorld();
        long currentTime = world.getTimeOfDay();
        long currentNormalized = currentTime % 24000;

        if (currentNormalized == TARGET_TIME) {
            GameRules.BooleanRule rule = world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE);
            if (rule.get()) {
                rule.set(false, player.getServer());
                player.sendMessage(Text.literal("☽ A lua atingiu o ápice. Drenando stamina..."), false);
            }
            spendStamina(traits, staminaCost());
            return;
        }

        if (!world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
            world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(true, player.getServer());
        }

        long distance = TARGET_TIME - currentNormalized;
        if (distance <= 0) distance += 24000;
        long add = Math.min(distance, SPEED_ADD);
        world.setTimeOfDay(currentTime + add);

        if (add >= distance) {
            world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(false, player.getServer());
            player.sendMessage(Text.literal("☽ A lua atingiu o ápice. Drenando stamina..."), false);
        }

        spendStamina(traits, staminaCost());
    }

    private void restoreDaylightCycle(ServerPlayerEntity player) {
        ServerWorld world = (ServerWorld) player.getWorld();
        if (!world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
            world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(true, player.getServer());
            player.sendMessage(Text.literal("☽ O ciclo dia/noite foi restaurado."), false);
        }
    }

    @Override
    public void reset(ServerPlayerEntity player) {
        restoreDaylightCycle(player);
    }
}
