package dev.joaq.ancestralpowers.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.joaq.ancestralpowers.npc.*;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

public class NPCCommands {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("npc")
            .requires(source -> source.hasPermissionLevel(2))
            
            .then(CommandManager.literal("create")
                .then(CommandManager.argument("type", StringArgumentType.word())
                    .executes(context -> {
                        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                        String typeStr = StringArgumentType.getString(context, "type");
                        
                        NPCType type = NPCType.fromString(typeStr);
                        NPCData npc = NPCManager.createAndSpawnNPC(player, type);
                        
                        player.sendMessage(Text.literal("§aNPC criado! ID: " + npc.getNpcId()), false);
                        player.sendMessage(Text.literal("§7Tipo: " + type.getId()), false);
                        return 1;
                    })
                    .then(CommandManager.argument("skinName", StringArgumentType.word())
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                            String typeStr = StringArgumentType.getString(context, "type");
                            String skinName = StringArgumentType.getString(context, "skinName");
                            
                            NPCType type = NPCType.fromString(typeStr);
                            NPCData npc = NPCManager.createAndSpawnNPCWithSkin(player, type, skinName);
                            
                            player.sendMessage(Text.literal("§aNPC criado com skin! ID: " + npc.getNpcId()), false);
                            player.sendMessage(Text.literal("§7Tipo: " + type.getId() + " | Skin: " + skinName), false);
                            return 1;
                        })
                    )
                )
            )
            
            .then(CommandManager.literal("list")
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                    List<NPCData> npcs = NPCManager.getAllNPCs(player);
                    
                    if (npcs.isEmpty()) {
                        player.sendMessage(Text.literal("§cVocê não possui NPCs"), false);
                        return 0;
                    }
                    
                    player.sendMessage(Text.literal("§6=== Seus NPCs ==="), false);
                    for (NPCData npc : npcs) {
                        String status = npc.getEntityUuid() != null ? "§aAtivo" : "§7Inativo";
                        player.sendMessage(Text.literal("§e" + npc.getType().getId() + " §7| " + status + " §7| ID: " + npc.getNpcId()), false);
                    }
                    return 1;
                })
            )
            
            .then(CommandManager.literal("remove")
                .then(CommandManager.argument("index", IntegerArgumentType.integer(0))
                    .executes(context -> {
                        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                        int index = IntegerArgumentType.getInteger(context, "index");
                        
                        List<NPCData> npcs = NPCManager.getAllNPCs(player);
                        if (index >= npcs.size()) {
                            player.sendMessage(Text.literal("§cÍndice inválido! Use /npc list"), false);
                            return 0;
                        }
                        
                        NPCData npc = npcs.get(index);
                        NPCManager.removeNPC(player, npc.getNpcId());
                        player.sendMessage(Text.literal("§aNPC removido!"), false);
                        return 1;
                    })
                )
            )
            
            .then(CommandManager.literal("removeall")
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                    int count = NPCManager.getNPCCount(player);
                    NPCManager.removeAllNPCs(player);
                    player.sendMessage(Text.literal("§a" + count + " NPCs removidos!"), false);
                    return 1;
                })
                .then(CommandManager.argument("type", StringArgumentType.word())
                    .executes(context -> {
                        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                        String typeStr = StringArgumentType.getString(context, "type");
                        NPCType type = NPCType.fromString(typeStr);
                        
                        int count = NPCManager.getNPCCountByType(player, type);
                        NPCManager.removeAllNPCsByType(player, type);
                        player.sendMessage(Text.literal("§a" + count + " NPCs do tipo " + type.getId() + " removidos!"), false);
                        return 1;
                    })
                )
            )
            
            .then(CommandManager.literal("tp")
                .then(CommandManager.argument("index", IntegerArgumentType.integer(0))
                    .executes(context -> {
                        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                        int index = IntegerArgumentType.getInteger(context, "index");
                        
                        List<NPCData> npcs = NPCManager.getAllNPCs(player);
                        if (index >= npcs.size()) {
                            player.sendMessage(Text.literal("§cÍndice inválido! Use /npc list"), false);
                            return 0;
                        }
                        
                        NPCData npc = npcs.get(index);
                        NPCManager.teleportToNPC(player, npc.getNpcId());
                        player.sendMessage(Text.literal("§aTeleportado para o NPC!"), false);
                        return 1;
                    })
                )
            )
            
            .then(CommandManager.literal("tphere")
                .then(CommandManager.argument("index", IntegerArgumentType.integer(0))
                    .executes(context -> {
                        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                        int index = IntegerArgumentType.getInteger(context, "index");
                        
                        List<NPCData> npcs = NPCManager.getAllNPCs(player);
                        if (index >= npcs.size()) {
                            player.sendMessage(Text.literal("§cÍndice inválido! Use /npc list"), false);
                            return 0;
                        }
                        
                        NPCData npc = npcs.get(index);
                        NPCManager.teleportNPCToPlayer(player, npc.getNpcId());
                        player.sendMessage(Text.literal("§aNPC teleportado até você!"), false);
                        return 1;
                    })
                )
            )
            
            .then(CommandManager.literal("info")
                .then(CommandManager.argument("index", IntegerArgumentType.integer(0))
                    .executes(context -> {
                        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                        int index = IntegerArgumentType.getInteger(context, "index");
                        
                        List<NPCData> npcs = NPCManager.getAllNPCs(player);
                        if (index >= npcs.size()) {
                            player.sendMessage(Text.literal("§cÍndice inválido! Use /npc list"), false);
                            return 0;
                        }
                        
                        NPCData npc = npcs.get(index);
                        player.sendMessage(Text.literal("§6=== Informações do NPC ==="), false);
                        player.sendMessage(Text.literal("§7ID: §f" + npc.getNpcId()), false);
                        player.sendMessage(Text.literal("§7Tipo: §f" + npc.getType().getId()), false);
                        player.sendMessage(Text.literal("§7Owner: §f" + npc.getOwnerName()), false);
                        player.sendMessage(Text.literal("§7Vida: §f" + npc.getHealth() + "/" + npc.getMaxHealth()), false);
                        player.sendMessage(Text.literal("§7XP: §f" + npc.getXpLevel() + " (Total: " + npc.getTotalXp() + ")"), false);
                        player.sendMessage(Text.literal("§7Posição: §f" + npc.getPosition()), false);
                        player.sendMessage(Text.literal("§7Dimensão: §f" + npc.getDimension()), false);
                        if (npc.getSkinName() != null) {
                            player.sendMessage(Text.literal("§7Skin: §f" + npc.getSkinName()), false);
                        }
                        if (npc.getMainPower() != null) {
                            player.sendMessage(Text.literal("§7Poder Principal: §f" + npc.getMainPower()), false);
                        }
                        if (npc.getSecondaryPower() != null) {
                            player.sendMessage(Text.literal("§7Poder Secundário: §f" + npc.getSecondaryPower()), false);
                        }
                        if (npc.getIntelligence() != null) {
                            player.sendMessage(Text.literal("§7Inteligência: §f" + npc.getIntelligence()), false);
                        }
                        return 1;
                    })
                )
            )
        );
    }
}