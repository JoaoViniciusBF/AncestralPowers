package dev.joaq.ancestralpowers.corpse.client.renderer;

import com.mojang.authlib.GameProfile;
import dev.joaq.ancestralpowers.corpse.CorpseConfig;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class DummyPlayer extends OtherClientPlayerEntity {

    private final byte model;

    public DummyPlayer(ClientWorld world, GameProfile profile, DefaultedList<ItemStack> equipment) {
        this(world, profile, equipment, (byte) 0x7F);
    }

    public DummyPlayer(ClientWorld world, GameProfile profile, DefaultedList<ItemStack> equipment, byte model) {
        super(world, profile);
        this.model = model;
        // Enable all model parts via reflection as fallback
        enableAllModelParts();
        if (CorpseConfig.renderEquipment) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack stack = equipment.get(slot.ordinal());
                if (!stack.isEmpty()) {
                    equipStack(slot, stack);
                }
            }
        }
        setPosition(0, 0, 0);
        prevX = 0;
        prevY = 0;
        prevZ = 0;
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return false;
    }

    private void enableAllModelParts() {
        try {
            // Try method names used in different mappings
            String[] methodNames = {"setModelPart", "method_31702", "b"};
            String[] classNames = {
                "net.minecraft.entity.player.PlayerEntity$ModelPart",
                "net.minecraft.entity.player.PlayerModelPart"
            };
            
            for (String className : classNames) {
                try {
                    Class<?> modelPartClass = Class.forName(className);
                    for (String methodName : methodNames) {
                        try {
                            java.lang.reflect.Method setPart = PlayerEntity.class.getDeclaredMethod(methodName, modelPartClass, boolean.class);
                            setPart.setAccessible(true);
                            for (Object part : modelPartClass.getEnumConstants()) {
                                setPart.invoke(this, part, true);
                            }
                            return; // Success
                        } catch (NoSuchMethodException ignored) {}
                    }
                } catch (ClassNotFoundException ignored) {}
            }
        } catch (Exception e) {
            // Fallback - no 3D layers
        }
    }
}