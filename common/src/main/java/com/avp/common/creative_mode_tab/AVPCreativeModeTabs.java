package com.avp.common.creative_mode_tab;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.avp.AVP;
import com.avp.AVPResources;
import com.avp.common.block.TempAVPBlocks;
import com.avp.common.creative_mode_tab.initializer.BlocksCreativeModeTabInitializer;
import com.avp.common.creative_mode_tab.initializer.ColoredBlocksCreativeModeTabInitializer;
import com.avp.common.creative_mode_tab.initializer.CombatCreativeModeTabInitializer;
import com.avp.common.creative_mode_tab.initializer.IngredientsCreativeModeTabInitializer;
import com.avp.common.creative_mode_tab.initializer.SpawnEggsCreativeModeTabInitializer;
import com.avp.common.creative_mode_tab.initializer.ToolsAndUtilitiesCreativeModeTabInitializer;
import com.avp.common.item.AVPArmorItems;
import com.avp.common.item.TempAVPItems;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.service.Services;

public class AVPCreativeModeTabs {

    private static final String BASE_PATH = "creativeModeTab";

    public static final ResourceKey<CreativeModeTab> BLOCKS_KEY = createResourceKey("blocks");

    public static final ResourceKey<CreativeModeTab> COLORED_BLOCKS_KEY = createResourceKey("colored_blocks");

    public static final ResourceKey<CreativeModeTab> COMBAT_KEY = createResourceKey("combat");

    public static final ResourceKey<CreativeModeTab> INGREDIENTS_KEY = createResourceKey("ingredients");

    public static final ResourceKey<CreativeModeTab> SPAWN_EGGS_KEY = createResourceKey("spawn_eggs");

    public static final ResourceKey<CreativeModeTab> TOOLS_AND_UTILITIES_KEY = createResourceKey("tools_and_utilities");

    public static final AVPDeferredHolder<CreativeModeTab> BLOCKS = register(
        BLOCKS_KEY,
        () -> new ItemStack(TempAVPBlocks.RESIN.get()),
        BlocksCreativeModeTabInitializer.OUTPUT_CONSUMER
    );

    public static final AVPDeferredHolder<CreativeModeTab> COLORED_BLOCKS = register(
        COLORED_BLOCKS_KEY,
        () -> new ItemStack(TempAVPBlocks.DYE_COLOR_TO_PLASTIC.get(DyeColor.WHITE).get()),
        ColoredBlocksCreativeModeTabInitializer.OUTPUT_CONSUMER
    );

    public static final AVPDeferredHolder<CreativeModeTab> COMBAT = register(
        COMBAT_KEY,
        () -> new ItemStack(AVPArmorItems.CHITIN_HELMET.get()),
        CombatCreativeModeTabInitializer.OUTPUT_CONSUMER
    );

    public static final AVPDeferredHolder<CreativeModeTab> INGREDIENTS = register(
        INGREDIENTS_KEY,
        () -> new ItemStack(TempAVPItems.PLATED_CHITIN.get()),
        IngredientsCreativeModeTabInitializer.OUTPUT_CONSUMER
    );

    public static final AVPDeferredHolder<CreativeModeTab> SPAWN_EGGS = register(
        SPAWN_EGGS_KEY,
        // TODO: Put a ovamorph spawn egg item for this tab icon.
        () -> new ItemStack(Items.EGG),
        SpawnEggsCreativeModeTabInitializer.OUTPUT_CONSUMER
    );

    public static final AVPDeferredHolder<CreativeModeTab> TOOLS_AND_UTILITIES = register(
        TOOLS_AND_UTILITIES_KEY,
        () -> new ItemStack(TempAVPItems.CANISTER.get()),
        ToolsAndUtilitiesCreativeModeTabInitializer.OUTPUT_CONSUMER
    );

    public static ResourceKey<CreativeModeTab> createResourceKey(String name) {
        return ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(),
            AVPResources.location(name)
        );
    }

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
