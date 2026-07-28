package dev.joaq.ancestralpowers.events;

import dev.joaq.ancestralpowers.components.MyComponents;
import dev.joaq.ancestralpowers.components.PlayerTraits;
import dev.joaq.ancestralpowers.entity.CloneEntity;
import dev.joaq.ancestralpowers.npc.NPCEntity;
import dev.joaq.ancestralpowers.npc.NPCManager;
import dev.joaq.ancestralpowers.registry.ModEffects;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SpectralFormHandler {

    public static void register() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                PlayerTraits traits = MyComponents.TRAITS.get(serverPlayer);
                if (traits.getInSpectralForm()) {
                    serverPlayer.sendMessage(Text.literal("§cVocê não pode quebrar blocos em forma espectral!"), true);
                    return false;
                }
            }
            return true;
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                PlayerTraits traits = MyComponents.TRAITS.get(serverPlayer);
                if (traits.getInSpectralForm()) {
                    serverPlayer.sendMessage(Text.literal("§cVocê não pode atacar em forma espectral!"), true);
                    return ActionResult.FAIL;
                }
            }
            return ActionResult.PASS;
        });
    }

    public static void checkStaminaAndReturnToBody(ServerPlayerEntity player) {
        PlayerTraits traits = MyComponents.TRAITS.get(player);

        if (!traits.getInSpectralForm()) {
            return;
        }

        if (traits.getStamina() <= 0) {
            returnToBody(player, traits);
        }
    }

    public static void returnToBody(ServerPlayerEntity player, PlayerTraits traits) {
        if (!traits.getInSpectralForm()) {
            return;
        }

        UUID bodyCloneId = traits.getSpectralBodyCloneId();
        if (bodyCloneId != null) {
            ServerWorld world = player.getServerWorld();
            Entity entity = world.getEntity(bodyCloneId);

            if (entity instanceof NPCEntity npcEntity) {
                player.teleport(world, 
                    npcEntity.getX(), 
                    npcEntity.getY(), 
                    npcEntity.getZ(), 
                    npcEntity.getYaw(), 
                    npcEntity.getPitch()
                );

                NPCManager.unregisterEntity(npcEntity.getNpcDataId());
                npcEntity.discard();
            }
        }

        restorePlayerInventory(player, traits);

        player.removeStatusEffect(ModEffects.SPECTRAL_FORM.value());
        player.removeStatusEffect(net.minecraft.entity.effect.StatusEffects.INVISIBILITY);

        player.getAbilities().allowFlying = false;
        player.getAbilities().flying = false;
        player.getAbilities().invulnerable = false;
        player.sendAbilitiesUpdate();

        traits.setInSpectralForm(false);
        traits.clearSpectralBodyCloneId();
        traits.clearSpectralBodyPosition();
        traits.clearSpectralBodyInventory();

        player.sendMessage(Text.literal("§aVocê retornou ao seu corpo físico!"), false);
    }

    private static void restorePlayerInventory(ServerPlayerEntity player, PlayerTraits traits) {
        NbtCompound inventoryNbt = traits.getSpectralBodyInventory();
        if (inventoryNbt == null) {
            return;
        }

        player.getInventory().clear();

        NbtList itemsList = inventoryNbt.getList("Items", 10);
        for (int i = 0; i < itemsList.size(); i++) {
            NbtCompound itemNbt = itemsList.getCompound(i);
            int slot = itemNbt.getInt("Slot");
            ItemStack stack = ItemStack.fromNbt(itemNbt);
            
            if (slot >= 0 && slot < player.getInventory().size()) {
                player.getInventory().setStack(slot, stack);
            }
        }

        if (inventoryNbt.contains("SelectedSlot")) {
            player.getInventory().selectedSlot = inventoryNbt.getInt("SelectedSlot");
        }
    }

    public static boolean canTakeDamage(ServerPlayerEntity player, DamageSource source) {
        PlayerTraits traits = MyComponents.TRAITS.get(player);
        return !traits.getInSpectralForm();
    }
}
