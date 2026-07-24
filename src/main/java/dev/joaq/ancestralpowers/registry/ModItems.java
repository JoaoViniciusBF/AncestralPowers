package dev.joaq.ancestralpowers.registry;

import dev.joaq.ancestralpowers.AncestralPowers;
import dev.joaq.ancestralpowers.item.SolarAxeItem;
import dev.joaq.ancestralpowers.item.LunarAxeItem;
import dev.joaq.ancestralpowers.item.DoubleJumpBootsItem;
import dev.joaq.ancestralpowers.item.DashBootsItem;
import dev.joaq.ancestralpowers.item.RepairRelicItem;
import dev.joaq.ancestralpowers.item.TimeCheckerItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;

public class ModItems {
    private static final ToolMaterial SOLAR_MATERIAL = new ToolMaterial(
        BlockTags.INCORRECT_FOR_IRON_TOOL,
        250,
        6.0f,
        2.0f,
        14,
        net.minecraft.registry.tag.ItemTags.IRON_TOOL_MATERIALS
    );

    private static final ToolMaterial LUNAR_MATERIAL = new ToolMaterial(
        BlockTags.INCORRECT_FOR_IRON_TOOL,
        250,
        6.0f,
        2.0f,
        14,
        net.minecraft.registry.tag.ItemTags.IRON_TOOL_MATERIALS
    );

    public static final Item TIME_CHECKER = register("time_checker",
        settings -> new TimeCheckerItem(settings),
        new Item.Settings().maxCount(1)
    );

    public static final Item REPAIR_RELIC = register("repair_relic",
        settings -> new RepairRelicItem(settings),
        new Item.Settings().maxCount(1)
    );

    public static final Item DOUBLE_JUMP_BOOTS = register("double_jump_boots",
        settings -> new DoubleJumpBootsItem(settings),
        new Item.Settings()
            .maxCount(1)
            .equippableUnswappable(EquipmentSlot.FEET)
    );

    public static final Item DASH_BOOTS = register("dash_boots",
        settings -> new DashBootsItem(settings),
        new Item.Settings()
            .maxCount(1)
            .equippableUnswappable(EquipmentSlot.FEET)
    );

    public static final Item SOLAR_AXE = register("solar_axe",
        settings -> new SolarAxeItem(SOLAR_MATERIAL, 5.0f, -3.0f, settings),
        new Item.Settings()
            .component(DataComponentTypes.ATTRIBUTE_MODIFIERS, 
                SolarAxeItem.createAttributeModifiers(SOLAR_MATERIAL, 5.0f, -3.0f))
    );

    public static final Item LUNAR_AXE = register("lunar_axe",
        settings -> new LunarAxeItem(LUNAR_MATERIAL, 5.0f, -3.0f, settings),
        new Item.Settings()
            .component(DataComponentTypes.ATTRIBUTE_MODIFIERS, 
                LunarAxeItem.createAttributeModifiers(LUNAR_MATERIAL, 5.0f, -3.0f))
    );

    private static Item register(String path, java.util.function.Function<Item.Settings, Item> factory, Item.Settings settings) {
        final RegistryKey<Item> registryKey = RegistryKey.of(RegistryKeys.ITEM, AncestralPowers.identifier(path));
        return Items.register(registryKey, factory, settings);
    }

    public static void register() {
        AncestralPowers.LOGGER.info("Registrando itens do mod " + AncestralPowers.MOD_ID);
    }
}
