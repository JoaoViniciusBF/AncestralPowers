package dev.joaq.ancestralpowers.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.joaq.ancestralpowers.client.InventoryLayoutConfig;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class InventoryPosCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("inventorypos")
                .then(ClientCommandManager.literal("crafting_grid")
                        .then(ClientCommandManager.literal("x")
                                .then(ClientCommandManager.argument("value", IntegerArgumentType.integer())
                                        .executes(ctx -> {
                                            InventoryLayoutConfig.craftingGridX = IntegerArgumentType.getInteger(ctx, "value");
                                            ctx.getSource().sendFeedback(Text.literal("Crafting Grid X: " + InventoryLayoutConfig.craftingGridX));
                                            return 1;
                                        })))
                        .then(ClientCommandManager.literal("y")
                                .then(ClientCommandManager.argument("value", IntegerArgumentType.integer())
                                        .executes(ctx -> {
                                            InventoryLayoutConfig.craftingGridY = IntegerArgumentType.getInteger(ctx, "value");
                                            ctx.getSource().sendFeedback(Text.literal("Crafting Grid Y: " + InventoryLayoutConfig.craftingGridY));
                                            return 1;
                                        }))))
                .then(ClientCommandManager.literal("crafting_result")
                        .then(ClientCommandManager.literal("x")
                                .then(ClientCommandManager.argument("value", IntegerArgumentType.integer())
                                        .executes(ctx -> {
                                            InventoryLayoutConfig.craftingResultX = IntegerArgumentType.getInteger(ctx, "value");
                                            ctx.getSource().sendFeedback(Text.literal("Crafting Result X: " + InventoryLayoutConfig.craftingResultX));
                                            return 1;
                                        })))
                        .then(ClientCommandManager.literal("y")
                                .then(ClientCommandManager.argument("value", IntegerArgumentType.integer())
                                        .executes(ctx -> {
                                            InventoryLayoutConfig.craftingResultY = IntegerArgumentType.getInteger(ctx, "value");
                                            ctx.getSource().sendFeedback(Text.literal("Crafting Result Y: " + InventoryLayoutConfig.craftingResultY));
                                            return 1;
                                        }))))
                .then(ClientCommandManager.literal("inventory")
                        .then(ClientCommandManager.literal("x")
                                .then(ClientCommandManager.argument("value", IntegerArgumentType.integer())
                                        .executes(ctx -> {
                                            InventoryLayoutConfig.inventoryOffsetX = IntegerArgumentType.getInteger(ctx, "value");
                                            ctx.getSource().sendFeedback(Text.literal("Inventory Offset X: " + InventoryLayoutConfig.inventoryOffsetX));
                                            return 1;
                                        })))
                        .then(ClientCommandManager.literal("y")
                                .then(ClientCommandManager.argument("value", IntegerArgumentType.integer())
                                        .executes(ctx -> {
                                            InventoryLayoutConfig.inventoryOffsetY = IntegerArgumentType.getInteger(ctx, "value");
                                            ctx.getSource().sendFeedback(Text.literal("Inventory Offset Y: " + InventoryLayoutConfig.inventoryOffsetY));
                                            return 1;
                                        }))))
                .then(ClientCommandManager.literal("helmet")
                        .then(ClientCommandManager.literal("x")
                                .then(ClientCommandManager.argument("value", IntegerArgumentType.integer())
                                        .executes(ctx -> {
                                            InventoryLayoutConfig.helmetSlotX = IntegerArgumentType.getInteger(ctx, "value");
                                            ctx.getSource().sendFeedback(Text.literal("Helmet X: " + InventoryLayoutConfig.helmetSlotX));
                                            return 1;
                                        })))
                        .then(ClientCommandManager.literal("y")
                                .then(ClientCommandManager.argument("value", IntegerArgumentType.integer())
                                        .executes(ctx -> {
                                            InventoryLayoutConfig.helmetSlotY = IntegerArgumentType.getInteger(ctx, "value");
                                            ctx.getSource().sendFeedback(Text.literal("Helmet Y: " + InventoryLayoutConfig.helmetSlotY));
                                            return 1;
                                        }))))
                .then(ClientCommandManager.literal("chestplate")
                        .then(ClientCommandManager.literal("x")
                                .then(ClientCommandManager.argument("value", IntegerArgumentType.integer())
                                        .executes(ctx -> {
                                            InventoryLayoutConfig.chestplateSlotX = IntegerArgumentType.getInteger(ctx, "value");
                                            ctx.getSource().sendFeedback(Text.literal("Chestplate X: " + InventoryLayoutConfig.chestplateSlotX));
                                            return 1;
                                        })))
                        .then(ClientCommandManager.literal("y")
                                .then(ClientCommandManager.argument("value", IntegerArgumentType.integer())
                                        .executes(ctx -> {
                                            InventoryLayoutConfig.chestplateSlotY = IntegerArgumentType.getInteger(ctx, "value");
                                            ctx.getSource().sendFeedback(Text.literal("Chestplate Y: " + InventoryLayoutConfig.chestplateSlotY));
                                            return 1;
                                        }))))
                .then(ClientCommandManager.literal("leggings")
                        .then(ClientCommandManager.literal("x")
                                .then(ClientCommandManager.argument("value", IntegerArgumentType.integer())
                                        .executes(ctx -> {
                                            InventoryLayoutConfig.leggingsSlotX = IntegerArgumentType.getInteger(ctx, "value");
                                            ctx.getSource().sendFeedback(Text.literal("Leggings X: " + InventoryLayoutConfig.leggingsSlotX));
                                            return 1;
                                        })))
                        .then(ClientCommandManager.literal("y")
                                .then(ClientCommandManager.argument("value", IntegerArgumentType.integer())
                                        .executes(ctx -> {
                                            InventoryLayoutConfig.leggingsSlotY = IntegerArgumentType.getInteger(ctx, "value");
                                            ctx.getSource().sendFeedback(Text.literal("Leggings Y: " + InventoryLayoutConfig.leggingsSlotY));
                                            return 1;
                                        }))))
                .then(ClientCommandManager.literal("boots")
                        .then(ClientCommandManager.literal("x")
                                .then(ClientCommandManager.argument("value", IntegerArgumentType.integer())
                                        .executes(ctx -> {
                                            InventoryLayoutConfig.bootsSlotX = IntegerArgumentType.getInteger(ctx, "value");
                                            ctx.getSource().sendFeedback(Text.literal("Boots X: " + InventoryLayoutConfig.bootsSlotX));
                                            return 1;
                                        })))
                        .then(ClientCommandManager.literal("y")
                                .then(ClientCommandManager.argument("value", IntegerArgumentType.integer())
                                        .executes(ctx -> {
                                            InventoryLayoutConfig.bootsSlotY = IntegerArgumentType.getInteger(ctx, "value");
                                            ctx.getSource().sendFeedback(Text.literal("Boots Y: " + InventoryLayoutConfig.bootsSlotY));
                                            return 1;
                                        }))))
                .then(ClientCommandManager.literal("offhand")
                        .then(ClientCommandManager.literal("x")
                                .then(ClientCommandManager.argument("value", IntegerArgumentType.integer())
                                        .executes(ctx -> {
                                            InventoryLayoutConfig.offhandSlotX = IntegerArgumentType.getInteger(ctx, "value");
                                            ctx.getSource().sendFeedback(Text.literal("Offhand X: " + InventoryLayoutConfig.offhandSlotX));
                                            return 1;
                                        })))
                        .then(ClientCommandManager.literal("y")
                                .then(ClientCommandManager.argument("value", IntegerArgumentType.integer())
                                        .executes(ctx -> {
                                            InventoryLayoutConfig.offhandSlotY = IntegerArgumentType.getInteger(ctx, "value");
                                            ctx.getSource().sendFeedback(Text.literal("Offhand Y: " + InventoryLayoutConfig.offhandSlotY));
                                            return 1;
                                        }))))
                .then(ClientCommandManager.literal("player")
                        .then(ClientCommandManager.literal("x")
                                .then(ClientCommandManager.argument("value", IntegerArgumentType.integer())
                                        .executes(ctx -> {
                                            InventoryLayoutConfig.playerRenderX = IntegerArgumentType.getInteger(ctx, "value");
                                            ctx.getSource().sendFeedback(Text.literal("Player render X: " + InventoryLayoutConfig.playerRenderX));
                                            return 1;
                                        }))))
                .then(ClientCommandManager.literal("reset")
                        .executes(ctx -> {
                            InventoryLayoutConfig.reset();
                            ctx.getSource().sendFeedback(Text.literal("Reset to default values"));
                            return 1;
                        }))
                .then(ClientCommandManager.literal("show")
                        .executes(ctx -> {
                            ctx.getSource().sendFeedback(Text.literal("§6=== Inventory Positions ==="));
                            ctx.getSource().sendFeedback(Text.literal("§eCrafting Grid: §f(" + InventoryLayoutConfig.craftingGridX + ", " + InventoryLayoutConfig.craftingGridY + ")"));
                            ctx.getSource().sendFeedback(Text.literal("§eCrafting Res: §f(" + InventoryLayoutConfig.craftingResultX + ", " + InventoryLayoutConfig.craftingResultY + ")"));
                            ctx.getSource().sendFeedback(Text.literal("§eInventory: §f(" + InventoryLayoutConfig.inventoryOffsetX + ", " + InventoryLayoutConfig.inventoryOffsetY + ")"));
                            ctx.getSource().sendFeedback(Text.literal("§eHelmet: §f(" + InventoryLayoutConfig.helmetSlotX + ", " + InventoryLayoutConfig.helmetSlotY + ")"));
                            ctx.getSource().sendFeedback(Text.literal("§eChestplate: §f(" + InventoryLayoutConfig.chestplateSlotX + ", " + InventoryLayoutConfig.chestplateSlotY + ")"));
                            ctx.getSource().sendFeedback(Text.literal("§eLeggings: §f(" + InventoryLayoutConfig.leggingsSlotX + ", " + InventoryLayoutConfig.leggingsSlotY + ")"));
                            ctx.getSource().sendFeedback(Text.literal("§eBoots: §f(" + InventoryLayoutConfig.bootsSlotX + ", " + InventoryLayoutConfig.bootsSlotY + ")"));
                            ctx.getSource().sendFeedback(Text.literal("§eOffhand: §f(" + InventoryLayoutConfig.offhandSlotX + ", " + InventoryLayoutConfig.offhandSlotY + ")"));
                            ctx.getSource().sendFeedback(Text.literal("§ePlayer X: §f" + InventoryLayoutConfig.playerRenderX));
                            return 1;
                        }))
        );
    }
}
