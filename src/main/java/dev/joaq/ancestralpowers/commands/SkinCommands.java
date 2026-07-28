package dev.joaq.ancestralpowers.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.joaq.ancestralpowers.components.MyComponents;
import dev.joaq.ancestralpowers.components.PlayerTraits;
import dev.joaq.ancestralpowers.skin.SkinManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class SkinCommands {
    
    public static void registerSkinCommands(com.mojang.brigadier.CommandDispatcher<net.minecraft.server.command.ServerCommandSource> dispatcher) {
        dispatcher.register(
                net.minecraft.server.command.CommandManager.literal("skin")
                        .then(net.minecraft.server.command.CommandManager.argument("skinName", StringArgumentType.word())
                                .executes(context -> {
                                    ServerPlayerEntity player = context.getSource().getPlayer();
                                    if (player == null) return 0;
                                    
                                    String skinName = StringArgumentType.getString(context, "skinName");
                                    return applySkin(player, skinName);
                                }))
        );
        
        dispatcher.register(
                net.minecraft.server.command.CommandManager.literal("skinreset")
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            if (player == null) return 0;
                            
                            return resetSkin(player);
                        })
        );
        
        dispatcher.register(
                net.minecraft.server.command.CommandManager.literal("skins")
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            if (player == null) return 0;
                            
                            return listSkins(player);
                        })
        );
        
        dispatcher.register(
                net.minecraft.server.command.CommandManager.literal("reloadskins")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> {
                            SkinManager.loadSkins(context.getSource().getServer());
                            context.getSource().sendFeedback(() -> Text.literal("§aSkins recarregadas com sucesso!"), true);
                            return 1;
                        })
        );
    }
    
    private static int applySkin(ServerPlayerEntity player, String skinName) {
        if (!SkinManager.hasSkin(skinName)) {
            if (SkinManager.isPending(skinName)) {
                player.sendMessage(Text.literal("§eSkin '" + skinName + "' está pendente de conversão."), false);
                player.sendMessage(Text.literal("§7A conversão automática falhou. Use /reloadskins para tentar novamente"), false);
                player.sendMessage(Text.literal("§7ou converta manualmente em https://mineskin.org/"), false);
                return 0;
            }
            
            player.sendMessage(Text.literal("§cSkin '" + skinName + "' não encontrada!"), false);
            player.sendMessage(Text.literal("§eUse /skins para ver skins disponíveis."), false);
            return 0;
        }
        
        SkinManager.SkinData skinData = SkinManager.getSkin(skinName);
        
        GameProfile profile = player.getGameProfile();
        profile.getProperties().removeAll("textures");
        profile.getProperties().put("textures", new Property("textures", skinData.value, skinData.signature));
        
        var server = player.getServer();
        var playerManager = server.getPlayerManager();
        
        for (ServerPlayerEntity otherPlayer : playerManager.getPlayerList()) {
            otherPlayer.networkHandler.sendPacket(new net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket(
                java.util.List.of(player.getUuid())
            ));
        }
        
        server.execute(() -> {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            for (ServerPlayerEntity otherPlayer : playerManager.getPlayerList()) {
                otherPlayer.networkHandler.sendPacket(new net.minecraft.network.packet.s2c.play.PlayerListS2CPacket(
                    net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Action.ADD_PLAYER,
                    player
                ));
            }
            
            player.requestRespawn();
        });
        
        player.sendMessage(Text.literal("§aSkin '" + skinName + "' aplicada com sucesso!"), false);
        player.sendMessage(Text.literal("§7Fonte: " + skinData.source), false);
        
        return 1;
    }
    
    private static int resetSkin(ServerPlayerEntity player) {
        player.sendMessage(Text.literal("§eA skin será resetada ao reconectar."), false);
        player.sendMessage(Text.literal("§7Ou use /skin [nome] para aplicar outra skin."), false);
        return 1;
    }
    
    private static int listSkins(ServerPlayerEntity player) {
        var skins = SkinManager.getAllSkins();
        
        if (skins.isEmpty()) {
            player.sendMessage(Text.literal("§eNenhuma skin customizada disponível."), false);
            player.sendMessage(Text.literal("§7Adicione arquivos .png na pasta skins/"), false);
            player.sendMessage(Text.literal("§7Conversão automática para JSON será tentada."), false);
            return 0;
        }
        
        player.sendMessage(Text.literal("§6=== Skins Disponíveis ==="), false);
        skins.forEach((name, data) -> {
            String sourceTag = switch(data.source) {
                case "json" -> "§a(JSON)";
                case "mineskin" -> "§b(Auto)";
                case "mineskin_auto" -> "§b(Auto)";
                default -> "§7(?)";
            };
            
            net.minecraft.text.MutableText text = Text.literal("§e• " + name + " " + sourceTag);
            text.setStyle(net.minecraft.text.Style.EMPTY
                    .withClickEvent(new net.minecraft.text.ClickEvent(
                            net.minecraft.text.ClickEvent.Action.RUN_COMMAND,
                            "/skin " + name))
                    .withHoverEvent(new net.minecraft.text.HoverEvent(
                            net.minecraft.text.HoverEvent.Action.SHOW_TEXT,
                            Text.literal("§aClique para aplicar\n§7Fonte: " + data.source))));
            
            player.sendMessage(text, false);
        });
        
        player.sendMessage(Text.literal("§7Total: " + skins.size() + " skins disponíveis"), false);
        player.sendMessage(Text.literal("§7Adicione .png na pasta skins/ para auto-conversão"), false);
        
        return 1;
    }
}
