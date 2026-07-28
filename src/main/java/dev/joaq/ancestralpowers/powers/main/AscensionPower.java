package dev.joaq.ancestralpowers.powers.main;

import dev.joaq.ancestralpowers.components.MyComponents;
import dev.joaq.ancestralpowers.components.PlayerTraits;
import dev.joaq.ancestralpowers.entity.CloneEntity;
import dev.joaq.ancestralpowers.npc.NPCEntity;
import dev.joaq.ancestralpowers.powers.PowerBase;
import dev.joaq.ancestralpowers.registry.ModEffects;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;

public class AscensionPower extends PowerBase {

    @Override
    protected void executeLogic(ServerPlayerEntity player, boolean activate, float stamina) {
        PlayerTraits traits = MyComponents.TRAITS.get(player);

        if (traits.getInSpectralForm()) {
            player.sendMessage(Text.literal("§cVocê já está em forma espectral!"), false);
            return;
        }

        savePlayerInventory(player, traits);

        NPCEntity spectralBody = CloneEntity.createClone(player, 0);
        spectralBody.setCustomName(Text.literal("§bCorpo de " + player.getName().getString()));
        spectralBody.setCustomNameVisible(true);

        traits.setSpectralBodyCloneId(spectralBody.getUuid());
        traits.setSpectralBodyPosition(player.getPos());
        traits.setInSpectralForm(true);

        player.getInventory().clear();

        player.addStatusEffect(new StatusEffectInstance(
                ModEffects.SPECTRAL_FORM.value(),
                Integer.MAX_VALUE,
                0,
                false,
                false,
                true
        ));

        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.INVISIBILITY,
                Integer.MAX_VALUE,
                0,
                false,
                false,
                false
        ));

        player.getAbilities().allowFlying = true;
        player.getAbilities().flying = true;
        player.getAbilities().invulnerable = true;
        player.sendAbilitiesUpdate();

        player.sendMessage(Text.literal("§aVocê ascendeu para a forma espectral!"), false);
        player.sendMessage(Text.literal("§eSeu corpo físico foi deixado para trás."), false);
    }

    private void savePlayerInventory(ServerPlayerEntity player, PlayerTraits traits) {
        NbtCompound inventoryNbt = new NbtCompound();
        NbtList itemsList = new NbtList();

        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (!stack.isEmpty()) {
                NbtCompound itemNbt = new NbtCompound();
                itemNbt.putInt("Slot", i);
                stack.writeNbt(itemNbt);
                itemsList.add(itemNbt);
            }
        }

        inventoryNbt.put("Items", itemsList);
        inventoryNbt.putInt("SelectedSlot", player.getInventory().selectedSlot);
        
        traits.setSpectralBodyInventory(inventoryNbt);
    }

    @Override
    protected float staminaCost() {
        return 30;
    }

    @Override
    public String ActivationType() {
        return "PRESS";
    }

    @Override
    protected void disablePowerSpecific(ServerPlayerEntity player) {

    }

    @Override
    public void apply(ServerPlayerEntity player, boolean activate, float stamina) {
        PlayerTraits traits = MyComponents.TRAITS.get(player);
        execute(player, activate, ActivationType(), traits, "Main");
    }
}
