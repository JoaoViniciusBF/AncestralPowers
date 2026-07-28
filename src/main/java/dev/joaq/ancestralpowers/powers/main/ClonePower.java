package dev.joaq.ancestralpowers.powers.main;

import dev.joaq.ancestralpowers.components.CloneData;
import dev.joaq.ancestralpowers.components.MyComponents;
import dev.joaq.ancestralpowers.components.PlayerTraits;
import dev.joaq.ancestralpowers.entity.CloneEntity;
import dev.joaq.ancestralpowers.powers.PowerBase;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ClonePower extends PowerBase {

    @Override
    protected void executeLogic(ServerPlayerEntity player, boolean activate, float stamina) {
        PlayerTraits traits = MyComponents.TRAITS.get(player);
        CloneData cloneData = traits.getCloneData();

        CloneData.Clone newClone = new CloneData.Clone(player);
        cloneData.addClone(newClone);

        int cloneNumber = cloneData.getCloneCount();
        
        ArmorStandEntity cloneEntity = CloneEntity.createClone(player, cloneNumber);
        newClone.entityUuid = cloneEntity.getUuid();

        player.sendMessage(Text.literal("§aClone #" + cloneNumber + " criado com sucesso!"), false);
        player.sendMessage(Text.literal("§eUse /switch " + cloneNumber + " para alternar para este clone."), false);
    }

    @Override
    protected float staminaCost() {
        return 50;
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
