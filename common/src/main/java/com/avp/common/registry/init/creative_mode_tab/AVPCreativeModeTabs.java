package com.avp.common.registry.init.creative_mode_tab;

import com.human.common.registry.init.block.HumanPlasticBlocks;
import com.human.common.registry.init.item.HumanSpawnEggItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.avp.AVP;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.block.AVPBlocks;
import com.avp.common.registry.init.creative_mode_tab.initializer.BlocksCreativeModeTabInitializer;
import com.avp.common.registry.init.creative_mode_tab.initializer.ColoredBlocksCreativeModeTabInitializer;
import com.avp.common.registry.init.creative_mode_tab.initializer.CombatCreativeModeTabInitializer;
import com.avp.common.registry.init.creative_mode_tab.initializer.IngredientsCreativeModeTabInitializer;
import com.avp.common.registry.init.creative_mode_tab.initializer.SpawnEggsCreativeModeTabInitializer;
import com.avp.common.registry.init.creative_mode_tab.initializer.ToolsAndUtilitiesCreativeModeTabInitializer;
import com.avp.common.registry.init.item.AVPArmorItems;
import com.avp.common.registry.init.item.AVPItems;
import com.avp.common.registry.key.AVPCreativeModeTabKeys;
import com.avp.service.Services;

public class AVPCreativeModeTabs {

    private static final String BASE_PATH = "creativeModeTab";

    public static final AVPDeferredHolder<CreativeModeTab> BLOCKS = register(
        AVPCreativeModeTabKeys.BLOCKS_KEY,
        () -> new ItemStack(AVPBlocks.BLUEPRINT_BLOCK.get()),
        BlocksCreativeModeTabInitializer.OUTPUT_CONSUMER
    );

    public static final AVPDeferredHolder<CreativeModeTab> COLORED_BLOCKS = register(
        AVPCreativeModeTabKeys.COLORED_BLOCKS_KEY,
        () -> new ItemStack(HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC.get(DyeColor.WHITE).get()),
        ColoredBlocksCreativeModeTabInitializer.OUTPUT_CONSUMER
    );

    public static final AVPDeferredHolder<CreativeModeTab> COMBAT = register(
        AVPCreativeModeTabKeys.COMBAT_KEY,
        () -> new ItemStack(AVPArmorItems.TACTICAL_HELMET.get()),
        CombatCreativeModeTabInitializer.OUTPUT_CONSUMER
    );

    public static final AVPDeferredHolder<CreativeModeTab> INGREDIENTS = register(
        AVPCreativeModeTabKeys.INGREDIENTS_KEY,
        () -> new ItemStack(AVPItems.TITANIUM_INGOT.get()),
        IngredientsCreativeModeTabInitializer.OUTPUT_CONSUMER
    );

    public static final AVPDeferredHolder<CreativeModeTab> SPAWN_EGGS = register(
        AVPCreativeModeTabKeys.SPAWN_EGGS_KEY,
        () -> new ItemStack(HumanSpawnEggItems.MARINE_SPAWN_EGG.get()),
        SpawnEggsCreativeModeTabInitializer.OUTPUT_CONSUMER
    );

    public static final AVPDeferredHolder<CreativeModeTab> TOOLS_AND_UTILITIES = register(
        AVPCreativeModeTabKeys.TOOLS_AND_UTILITIES_KEY,
        () -> new ItemStack(AVPItems.CANISTER.get()),
        ToolsAndUtilitiesCreativeModeTabInitializer.OUTPUT_CONSUMER
    );

    public static AVPDeferredHolder<CreativeModeTab> register(
        ResourceKey<CreativeModeTab> resourceKey,
        Supplier<ItemStack> iconSupplier,
        Consumer<CreativeModeTab.Output> outputConsumer
    ) {
        var path = resourceKey.location().getPath();

        return Services.REGISTRY.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            path,
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                .icon(iconSupplier)
                .title(Component.translatable(BASE_PATH + "." + AVP.MOD_ID + "." + path))
                .displayItems((itemDisplayParameters, output) -> outputConsumer.accept(output))
                .build()
        );
    }

    public static void initialize() {}
}
