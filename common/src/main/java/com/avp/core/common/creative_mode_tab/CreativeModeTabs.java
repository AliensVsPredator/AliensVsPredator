package com.avp.core.common.creative_mode_tab;

import com.avp.core.common.creative_mode_tab.initializer.BlocksCreativeModeTabInitializer;
import com.avp.core.common.creative_mode_tab.initializer.ColoredBlocksCreativeModeTabInitializer;
import com.avp.core.common.creative_mode_tab.initializer.CombatCreativeModeTabInitializer;
import com.avp.core.common.creative_mode_tab.initializer.IngredientsCreativeModeTabInitializer;
import com.avp.core.common.creative_mode_tab.initializer.SpawnEggsCreativeModeTabInitializer;
import com.avp.core.common.creative_mode_tab.initializer.ToolsAndUtilitiesCreativeModeTabInitializer;
import com.avp.core.platform.service.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.avp.core.AVP;
import com.avp.core.AVPResources;
import com.avp.core.common.block.AVPBlocks;
import com.avp.core.common.item.AVPItems;
import com.avp.core.common.item.ArmorItems;
import com.avp.core.common.item.SpawnEggItems;

public class CreativeModeTabs {

    private static final String BASE_PATH = "creativeModeTab";

    public static final ResourceKey<CreativeModeTab> BLOCKS_KEY = createResourceKey("blocks");

    public static final ResourceKey<CreativeModeTab> COLORED_BLOCKS_KEY = createResourceKey("colored_blocks");

    public static final ResourceKey<CreativeModeTab> COMBAT_KEY = createResourceKey("combat");

    public static final ResourceKey<CreativeModeTab> INGREDIENTS_KEY = createResourceKey("ingredients");

    public static final ResourceKey<CreativeModeTab> SPAWN_EGGS_KEY = createResourceKey("spawn_eggs");

    public static final ResourceKey<CreativeModeTab> TOOLS_AND_UTILITIES_KEY = createResourceKey("tools_and_utilities");

    public static final Supplier<CreativeModeTab> BLOCKS = register(BLOCKS_KEY, BlocksCreativeModeTabInitializer.CONSUMER, () -> new ItemStack(AVPBlocks.RESIN.get()));

    public static final Supplier<CreativeModeTab> COLORED_BLOCKS = register(
        COLORED_BLOCKS_KEY,
        ColoredBlocksCreativeModeTabInitializer.CONSUMER,
        () -> new ItemStack(AVPBlocks.DYE_COLOR_TO_PLASTIC.get(DyeColor.WHITE).get())
    );

    public static final Supplier<CreativeModeTab> COMBAT = register(COMBAT_KEY, CombatCreativeModeTabInitializer.CONSUMER, () -> new ItemStack(ArmorItems.CHITIN_HELMET.get()));

    public static final Supplier<CreativeModeTab> INGREDIENTS = register(INGREDIENTS_KEY, IngredientsCreativeModeTabInitializer.CONSUMER, () -> new ItemStack(AVPItems.PLATED_CHITIN.get()));

    public static final Supplier<CreativeModeTab> SPAWN_EGGS = register(SPAWN_EGGS_KEY, SpawnEggsCreativeModeTabInitializer.CONSUMER, () -> new ItemStack(SpawnEggItems.OVAMORPH_SPAWN_EGG.get()));

    public static final Supplier<CreativeModeTab> TOOLS_AND_UTILITIES = register(
        TOOLS_AND_UTILITIES_KEY,
        ToolsAndUtilitiesCreativeModeTabInitializer.CONSUMER,
        () -> new ItemStack(AVPItems.ROYAL_JELLY_CANISTER.get())
    );

    public static void initialize() {}

    private static ResourceKey<CreativeModeTab> createResourceKey(String name) {
        return ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(),
            AVPResources.location(name)
        );
    }

    private static Supplier<CreativeModeTab> register(ResourceKey<CreativeModeTab> resourceKey, Consumer<CreativeModeTab.Output> outputConsumer, Supplier<ItemStack> iconSupplier) {
        var path = resourceKey.location().getPath();

        // TODO: Ensure that these row/column values work properly.
        return Services.REGISTRY.registerCreativeModeTab(path, () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .icon(iconSupplier)
            .title(Component.translatable(BASE_PATH + "." + AVP.MOD_ID + "." + path))
            .displayItems((parameters, output) -> outputConsumer.accept(output))
            .build());
    }
}
