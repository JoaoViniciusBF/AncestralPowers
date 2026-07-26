package dev.joaq.ancestralpowers.powers.main;

import dev.joaq.ancestralpowers.components.MyComponents;
import dev.joaq.ancestralpowers.components.PlayerTraits;
import dev.joaq.ancestralpowers.powers.PowerBase;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SolarAccelerationPower extends PowerBase {

    private static final long TARGET_TIME = 6000;

    // Velocidade minima no inicio/fim da rampa (evita ficar "parado" perto do alvo)
    private static final double MIN_SPEED = 5.0;
    // Velocidade maxima no pico (metade do trajeto)
    private static final double PEAK_SPEED = 140.0;

    // Guarda, por jogador, a distancia total que existia quando o poder foi ativado.
    // Serve de referencia (100% do percurso) para calcular o progresso a cada tick.
    private static final Map<UUID, Long> initialDistanceByPlayer = new ConcurrentHashMap<>();

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
        clearRamp(player);
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
            clearRamp(player);
            return;
        }

        if (stamina < staminaCost()) {
            traits.setActPower_main(false);
            restoreDaylightCycle(player);
            clearRamp(player);
            player.sendMessage(Text.literal("☀ Sua stamina se esgotou. O tempo solar foi desativado."), false);
            return;
        }

        ServerWorld world = (ServerWorld) player.getWorld();
        long currentTime = world.getTimeOfDay();
        long currentNormalized = currentTime % 24000;

        // Ja estamos exatamente no meio-dia
        if (currentNormalized == TARGET_TIME) {
            GameRules.BooleanRule rule = world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE);
            if (rule.get()) {
                rule.set(false, player.getServer());
                player.sendMessage(Text.literal("☀ O sol atingiu o zênite. Drenando stamina..."), false);
            }
            clearRamp(player);
            spendStamina(traits, staminaCost());
            return;
        }

        if (!world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
            world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(true, player.getServer());
        }

        long remaining = TARGET_TIME - currentNormalized;
if (remaining <= 0) remaining += 24000;
final long remainingFinal = remaining;

// Se ainda nao ha uma distancia inicial registrada para esta ativacao,
// guardamos agora — ela representa 100% do percurso.
long initialDistance = initialDistanceByPlayer.computeIfAbsent(player.getUuid(), id -> remainingFinal);

        // Progresso do trajeto: 0.0 = acabou de ativar, 1.0 = chegando no meio-dia
        double traveled = initialDistance - remaining;
        double progress = initialDistance > 0 ? traveled / (double) initialDistance : 1.0;
        progress = Math.max(0.0, Math.min(1.0, progress));

        // Curva senoidal: comeca em MIN_SPEED, sobe ate PEAK_SPEED no meio do trajeto,
        // e desce de volta para MIN_SPEED perto do alvo.
        double speed = MIN_SPEED + (PEAK_SPEED - MIN_SPEED) * Math.sin(Math.PI * progress);

        long add = Math.round(speed);
        if (add < 1) add = 1;
        add = Math.min(remaining, add);

        world.setTimeOfDay(currentTime + add);

        if (add >= remaining) {
            world.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(false, player.getServer());
            player.sendMessage(Text.literal("☀ O sol atingiu o zênite. Drenando stamina..."), false);
            clearRamp(player);
        }

        spendStamina(traits, staminaCost());
    }

    private void clearRamp(ServerPlayerEntity player) {
        initialDistanceByPlayer.remove(player.getUuid());
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
        clearRamp(player);
    }
}