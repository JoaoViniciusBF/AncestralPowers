package dev.joaq.ancestralpowers.powers.main;

import dev.joaq.ancestralpowers.components.MyComponents;
import dev.joaq.ancestralpowers.components.PlayerTraits;
import dev.joaq.ancestralpowers.powers.PowerBase;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;

public class SolarAccelerationPower extends PowerBase {

    private static final long TARGET_TIME = 6000;
    private static final int SPEED_ADD = 2;

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
            player.sendMessage(Text.literal("☀ Sua stamina se esgotou. O tempo solar foi desativado."), false);
            return;
        }

        ServerWorld world = (ServerWorld) player.getWorld();
        MinecraftServer server = player.getServer();
        long currentTime = world.getTimeOfDay();
        long currentNormalized = currentTime % 24000;

        if (currentNormalized == TARGET_TIME) {
            GameRules.BooleanRule rule = world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE);
            if (rule.get()) {
                rule.set(false, server);
                player.sendMessage(Text.literal("☀ O sol atingiu o zênite. Drenando stamina..."), false);
            }
            spendStamina(traits, staminaCost());
            return;
        }

        if (!world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
            world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(true, server);
        }

        long prevNormalized = currentNormalized;
        long newTime = currentTime + SPEED_ADD;
        world.setTimeOfDay(newTime);

        long newNormalized = newTime % 24000;
        if (prevNormalized < TARGET_TIME && newNormalized >= TARGET_TIME) {
            long targetAbsolute = (currentTime / 24000) * 24000 + TARGET_TIME;
            world.setTimeOfDay(targetAbsolute);
            world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(false, server);
            player.sendMessage(Text.literal("☀ O sol atingiu o zênite. Drenando stamina..."), false);
        }

        spendStamina(traits, staminaCost());
    }

    private void restoreDaylightCycle(ServerPlayerEntity player) {
        ServerWorld world = (ServerWorld) player.getWorld();
        if (!world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
            world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(true, player.getServer());
            player.sendMessage(Text.literal("☀ O ciclo dia/noite foi restaurado."), false);
        }
    }

    @Override
    public void reset(ServerPlayerEntity player) {
        restoreDaylightCycle(player);
    }
}
