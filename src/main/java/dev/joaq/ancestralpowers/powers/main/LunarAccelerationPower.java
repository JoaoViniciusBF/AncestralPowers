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
    private static final int SPEED_ADD = 2;

    @Override
    protected float staminaCost() {
        return 2.0f;
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
        // A aceleracao e feita via apply() override
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
            world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(false, null);
            spendStamina(traits, staminaCost());
            return;
        }

        GameRules.BooleanRule rule = world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE);
        if (!rule.get()) {
            rule.set(true, null);
        }

        long prevNormalized = currentNormalized;
        world.setTimeOfDay(currentTime + SPEED_ADD);

        long newNormalized = world.getTimeOfDay() % 24000;
        if (prevNormalized < TARGET_TIME && newNormalized >= TARGET_TIME) {
            long targetAbsolute = (currentTime / 24000) * 24000 + TARGET_TIME;
            world.setTimeOfDay(targetAbsolute);
            world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(false, null);
            player.sendMessage(Text.literal("☽ A lua atingiu o ápice. Drenando stamina..."), false);
        }

        spendStamina(traits, staminaCost());
    }

    private void restoreDaylightCycle(ServerPlayerEntity player) {
        ServerWorld world = (ServerWorld) player.getWorld();
        GameRules.BooleanRule rule = world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE);
        if (!rule.get()) {
            rule.set(true, null);
        }
    }

    @Override
    public void reset(ServerPlayerEntity player) {
        restoreDaylightCycle(player);
    }
}
