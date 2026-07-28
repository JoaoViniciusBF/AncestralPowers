package dev.joaq.ancestralpowers.commands;

import dev.joaq.ancestralpowers.components.MyComponents;
import dev.joaq.ancestralpowers.components.PlayerTraits;
import dev.joaq.ancestralpowers.dimensions.ModDimensions;
import dev.joaq.ancestralpowers.dimensions.PersonalDimensionStructure;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.joaq.ancestralpowers.powers.PowersManager;
import dev.joaq.ancestralpowers.util.RandomUtils;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class ModCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            CloneCommands.registerSwitchCommand(dispatcher);
            SkinCommands.registerSkinCommands(dispatcher);

            // Comando /gettraits
            dispatcher.register(
                    CommandManager.literal("gettraits")
                            .requires(source -> source.hasPermissionLevel(2))
                            .executes(context -> {
                                ServerCommandSource source = context.getSource();

                                ServerPlayerEntity player = source.getPlayer();
                                if (player == null) {
                                    source.sendFeedback(() -> Text.literal("Apenas jogadores podem usar este comando!"), false);
                                    return 0;
                                }

                                PlayerTraits traits = MyComponents.TRAITS.get(player);

                                player.sendMessage(Text.literal(
                                        "Seus poderes: " + traits.getMovementPower() + " | " +
                                                traits.getMainPower() + " | " + traits.getIntelligence()
                                ), false);

                                return 1;
                            })
            );
            dispatcher.register(
                    CommandManager.literal("gotopersonal")
                            .requires(source -> source.hasPermissionLevel(2))
                            .executes(context -> {
                                ServerPlayerEntity player = context.getSource().getPlayer();
                                if (player == null) return 0;

                                ServerWorld world = player.getServer().getWorld(ModDimensions.PERSONAL_WORLD_KEY);
                                if (world == null) {
                                    player.sendMessage(Text.of("Dimensão pessoal não encontrada!"), false);
                                    return 0;
                                }

                                PersonalDimensionStructure.teleportToPersonalDimension(player, null);
                                player.sendMessage(Text.of("Teleportado para sua dimensão pessoal."), false);
                                return 1;
                            })
            );

            // Comando /rerolltraits
            dispatcher.register(
                    CommandManager.literal("rerolltraits")
                            .requires(source -> source.hasPermissionLevel(2))
                            .executes(context -> {
                                ServerCommandSource source = context.getSource();

                                ServerPlayerEntity player2 = source.getPlayer();
                                if (player2 == null) {
                                    source.sendFeedback(() -> Text.literal("Apenas jogadores podem usar este comando!"), false);
                                    return 0;
                                }

                                PowersManager.resetAll(player2);

                                PlayerTraits traits = MyComponents.TRAITS.get(player2);

                                traits.setMainPower(RandomUtils.randomMain());
                                traits.setMovementPower(RandomUtils.randomMovement(traits.getMainPower()));
                                traits.setIntelligence(RandomUtils.randomIntelligence());

                                // Informa o jogador
                                player2.sendMessage(Text.literal(
                                        "Novos poderes: " + traits.getMovementPower() + " | " +
                                                traits.getMainPower() + " | " + traits.getIntelligence()
                                ), false);

                                return 1;
                            })
            );

            dispatcher.register(
                    CommandManager.literal("powers")
                            .requires(source -> source.hasPermissionLevel(2))
                            .executes(context -> {
                                ServerPlayerEntity player = context.getSource().getPlayer();
                                if (player == null) return 0;

                                player.sendMessage(Text.literal("§6=== Seletor de Poderes ==="), false);
                                sendPowerOption(player, "Super Força");
                                sendPowerOption(player, "Imortalidade");
                                sendPowerOption(player, "Fireball");
                                sendPowerOption(player, "SuperTeleporteMain");
                                sendPowerOption(player, "Scale");
                                sendPowerOption(player, "SuperSpeed");
                                sendPowerOption(player, "Suppressor");
                                sendPowerOption(player, "ArenaPower");
                                sendPowerOption(player, "Clone");
                                return 1;
                            })
            );

            dispatcher.register(
                    CommandManager.literal("setpower")
                            .requires(source -> source.hasPermissionLevel(2))
                            .then(CommandManager.argument("power", StringArgumentType.greedyString())
                                    .executes(context -> {
                                        ServerPlayerEntity player = context.getSource().getPlayer();
                                        if (player == null) return 0;

                                        String power = StringArgumentType.getString(context, "power");
                                        PlayerTraits traits = MyComponents.TRAITS.get(player);
                                        PowersManager.resetAll(player);
                                        traits.setMainPower(power);
                                        player.sendMessage(Text.literal("§aPoder principal definido como: " + power), false);
                                        return 1;
                                    }))
            );
        });
    }

    private static void sendPowerOption(ServerPlayerEntity player, String power) {
        MutableText text = Text.literal("§e[ " + power + " ]")
                .setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/setpower " + power)));
        player.sendMessage(text, false);
    }
}

