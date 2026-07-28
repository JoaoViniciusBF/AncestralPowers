package dev.joaq.ancestralpowers.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.joaq.ancestralpowers.components.CloneData;
import dev.joaq.ancestralpowers.components.MyComponents;
import dev.joaq.ancestralpowers.components.PlayerTraits;
import dev.joaq.ancestralpowers.entity.CloneEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CloneCommands {

    public static void registerSwitchCommand(com.mojang.brigadier.CommandDispatcher<net.minecraft.server.command.ServerCommandSource> dispatcher) {
        dispatcher.register(
                net.minecraft.server.command.CommandManager.literal("switch")
                        .then(net.minecraft.server.command.CommandManager.argument("cloneNumber", IntegerArgumentType.integer(1))
                                .executes(context -> {
                                    ServerPlayerEntity player = context.getSource().getPlayer();
                                    if (player == null) return 0;

                                    int cloneNumber = IntegerArgumentType.getInteger(context, "cloneNumber");
                                    return switchToClone(player, cloneNumber - 1);
                                }))
        );

        dispatcher.register(
                net.minecraft.server.command.CommandManager.literal("listclones")
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            if (player == null) return 0;

                            return listClones(player);
                        })
        );

        dispatcher.register(
                net.minecraft.server.command.CommandManager.literal("deleteclone")
                        .then(net.minecraft.server.command.CommandManager.argument("cloneNumber", IntegerArgumentType.integer(1))
                                .executes(context -> {
                                    ServerPlayerEntity player = context.getSource().getPlayer();
                                    if (player == null) return 0;

                                    int cloneNumber = IntegerArgumentType.getInteger(context, "cloneNumber");
                                    return deleteClone(player, cloneNumber - 1);
                                }))
        );
    }

    private static int switchToClone(ServerPlayerEntity player, int cloneIndex) {
        PlayerTraits traits = MyComponents.TRAITS.get(player);
        CloneData cloneData = traits.getCloneData();

        if (cloneIndex < 0 || cloneIndex >= cloneData.getCloneCount()) {
            player.sendMessage(Text.literal("§cClone #" + (cloneIndex + 1) + " não existe!"), false);
            return 0;
        }

        CloneData.Clone targetClone = cloneData.getClone(cloneIndex);
        CloneData.Clone currentState = new CloneData.Clone(player);

        if (cloneData.getActiveCloneIndex() >= 0 && cloneData.getActiveCloneIndex() < cloneData.getCloneCount()) {
            CloneData.Clone oldClone = cloneData.getClones().get(cloneData.getActiveCloneIndex());
            oldClone.position = player.getBlockPos();
            oldClone.dimension = player.getWorld().getRegistryKey().getValue().toString();
            
            ArmorStandEntity cloneEntity = CloneEntity.createClone(player, cloneData.getActiveCloneIndex() + 1);
            oldClone.entityUuid = cloneEntity.getUuid();
            
            cloneData.getClones().set(cloneData.getActiveCloneIndex(), oldClone);
        }

        if (targetClone.entityUuid != null) {
            RegistryKey<World> dimensionKey = RegistryKey.of(RegistryKeys.WORLD, new Identifier(targetClone.dimension));
            ServerWorld targetWorld = player.getServer().getWorld(dimensionKey);
            if (targetWorld != null) {
                Entity entity = targetWorld.getEntity(targetClone.entityUuid);
                if (entity instanceof ArmorStandEntity) {
                    entity.discard();
                }
            }
        }

        applyCloneToPlayer(player, targetClone);
        cloneData.setActiveCloneIndex(cloneIndex);

        player.sendMessage(Text.literal("§aAlternado para o Clone #" + (cloneIndex + 1) + "!"), false);
        return 1;
    }

    private static void applyCloneToPlayer(ServerPlayerEntity player, CloneData.Clone clone) {
        player.getInventory().clear();
        NbtList inventoryList = clone.inventory.getList("Inventory", 10);
        player.getInventory().readNbt(inventoryList);

        player.getEnderChestInventory().clear();
        NbtList enderList = clone.enderChest.getList("EnderItems", 10);
        player.getEnderChestInventory().readNbtList(enderList);

        player.setHealth(clone.health);
        player.getHungerManager().setFoodLevel(clone.food);
        player.getHungerManager().setSaturationLevel(clone.saturation);
        player.experienceLevel = clone.xpLevel;
        player.experienceProgress = clone.xpProgress;

        player.clearStatusEffects();
        NbtList effectsList = clone.effects.getList("effects", 10);
        for (int i = 0; i < effectsList.size(); i++) {
            NbtCompound effectNbt = effectsList.getCompound(i);
        }

        RegistryKey<World> dimensionKey = RegistryKey.of(RegistryKeys.WORLD, new Identifier(clone.dimension));
        ServerWorld targetWorld = player.getServer().getWorld(dimensionKey);
        
        if (targetWorld != null && targetWorld != player.getWorld()) {
            Vec3d pos = new Vec3d(clone.position.getX() + 0.5, clone.position.getY(), clone.position.getZ() + 0.5);
            player.teleport(targetWorld, pos.x, pos.y, pos.z, player.getYaw(), player.getPitch());
        } else {
            player.teleport(clone.position.getX() + 0.5, clone.position.getY(), clone.position.getZ() + 0.5);
        }
    }

    private static int listClones(ServerPlayerEntity player) {
        PlayerTraits traits = MyComponents.TRAITS.get(player);
        CloneData cloneData = traits.getCloneData();

        int cloneCount = cloneData.getCloneCount();
        if (cloneCount == 0) {
            player.sendMessage(Text.literal("§eVocê não possui clones."), false);
            return 0;
        }

        player.sendMessage(Text.literal("§6=== Seus Clones ==="), false);
        for (int i = 0; i < cloneCount; i++) {
            CloneData.Clone clone = cloneData.getClone(i);
            String active = (i == cloneData.getActiveCloneIndex()) ? " §a(Ativo)" : "";
            player.sendMessage(Text.literal(
                    "§e#" + (i + 1) + active + " §7- HP: " + clone.health + 
                    " | XP: " + clone.xpLevel + " | Pos: " + clone.position.toShortString()
            ), false);
        }

        return 1;
    }

    private static int deleteClone(ServerPlayerEntity player, int cloneIndex) {
        PlayerTraits traits = MyComponents.TRAITS.get(player);
        CloneData cloneData = traits.getCloneData();

        if (cloneIndex < 0 || cloneIndex >= cloneData.getCloneCount()) {
            player.sendMessage(Text.literal("§cClone #" + (cloneIndex + 1) + " não existe!"), false);
            return 0;
        }

        if (cloneIndex == cloneData.getActiveCloneIndex()) {
            player.sendMessage(Text.literal("§cVocê não pode deletar o clone ativo!"), false);
            return 0;
        }

        cloneData.removeClone(cloneIndex);
        player.sendMessage(Text.literal("§aClone #" + (cloneIndex + 1) + " deletado com sucesso!"), false);
        return 1;
    }
}
