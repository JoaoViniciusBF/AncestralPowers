package dev.joaq.ancestralpowers.registry;

import dev.joaq.ancestralpowers.AncestralPowers;
import dev.joaq.ancestralpowers.item.SolarAxeItem;
import dev.joaq.ancestralpowers.item.LunarAxeItem;
import dev.joaq.ancestralpowers.item.DoubleJumpBootsItem;
import dev.joaq.ancestralpowers.item.DashBootsItem;
import dev.joaq.ancestralpowers.item.RepairRelicItem;
import dev.joaq.ancestralpowers.item.TimeCheckerItem;
import dev.joaq.ancestralpowers.item.EffectDaggerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Formatting;

public class ModItems {
    private static final Object SOLAR_MATERIAL = ToolMaterials.IRON;
    private static final Object LUNAR_MATERIAL = ToolMaterials.IRON;

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
    );

    public static final Item DASH_BOOTS = register("dash_boots",
        settings -> new DashBootsItem(settings),
        new Item.Settings()
            .maxCount(1)
    );

    public static final Item SOLAR_AXE = register("solar_axe",
        settings -> new SolarAxeItem((net.minecraft.item.ToolMaterial) SOLAR_MATERIAL, 5.0f, 1.0f, settings),
        new Item.Settings()
    );

    public static final Item LUNAR_AXE = register("lunar_axe",
        settings -> new LunarAxeItem((net.minecraft.item.ToolMaterial) LUNAR_MATERIAL, 5.0f, 1.0f, settings),
        new Item.Settings()
    );

    public static final Item AFFLICTION_DAGGER = register("affliction_dagger",
        settings -> new EffectDaggerItem(ToolMaterials.IRON, 2.0f, 1.5f, settings, false, Formatting.RED, Formatting.DARK_RED),
        new Item.Settings()
    );

    public static final Item INVERSION_DAGGER = register("inversion_dagger",
        settings -> new EffectDaggerItem(ToolMaterials.IRON, 2.0f, 1.5f, settings, true, Formatting.LIGHT_PURPLE, Formatting.DARK_PURPLE),
        new Item.Settings()
    );

    private static Item register(String path, java.util.function.Function<Item.Settings, Item> factory, Item.Settings settings) {
        final Identifier id = AncestralPowers.identifier(path);
        Item item = factory.apply(settings);
        return Registry.register(Registries.ITEM, id, item);
    }

    public static void register() {
        AncestralPowers.LOGGER.info("Registrando itens do mod " + AncestralPowers.MOD_ID);
    }
}
