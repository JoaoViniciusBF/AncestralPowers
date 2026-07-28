package dev.joaq.ancestralpowers.registry;

import dev.joaq.ancestralpowers.AncestralPowers;
import dev.joaq.ancestralpowers.item.*;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final RegistryKey<ItemGroup> ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(AncestralPowers.MOD_ID, "items"));

    public static final Item REPAIR_RELIC = register("repair_relic",
        settings -> new RepairRelicItem(settings),
        new Item.Settings().maxCount(1)
    );

    public static final Item DOUBLE_JUMP_BOOTS = register("double_jump_boots",
        settings -> new DoubleJumpBootsItem(settings),
        new Item.Settings().maxCount(1)
    );

    public static final Item DASH_BOOTS = register("dash_boots",
        settings -> new DashBootsItem(settings),
        new Item.Settings().maxCount(1)
    );

    public static final Item SOLAR_AXE = register("solar_axe",
        settings -> new SolarAxeItem(ToolMaterials.IRON, 5.0f, 1.0f, settings),
        new Item.Settings()
    );

    public static final Item LUNAR_AXE = register("lunar_axe",
        settings -> new LunarAxeItem(ToolMaterials.IRON, 5.0f, 1.0f, settings),
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
        Registry.register(Registries.ITEM_GROUP, ITEM_GROUP, FabricItemGroup.builder()
                .icon(() -> new ItemStack(REPAIR_RELIC))
                .displayName(Text.literal("Ancestral Powers"))
                .entries((context, entries) -> {
                    entries.add(REPAIR_RELIC);
                    entries.add(DOUBLE_JUMP_BOOTS);
                    entries.add(DASH_BOOTS);
                    entries.add(SOLAR_AXE);
                    entries.add(LUNAR_AXE);
                    entries.add(AFFLICTION_DAGGER);
                    entries.add(INVERSION_DAGGER);
                })
                .build());
        AncestralPowers.LOGGER.info("Registrando itens do mod " + AncestralPowers.MOD_ID);
    }
}
