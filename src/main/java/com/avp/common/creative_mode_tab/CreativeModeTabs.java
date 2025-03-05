package com.avp.common.creative_mode_tab;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

import com.avp.AVP;
import com.avp.AVPResources;
import com.avp.common.block.AVPBlocks;
import com.avp.common.item.AVPItems;
import com.avp.common.item.ArmorItems;
import com.avp.common.item.SpawnEggItems;

public class CreativeModeTabs {

    private static final String BASE_PATH = "creativeModeTab";

    public static final ResourceKey<CreativeModeTab> BLOCKS_KEY = createResourceKey("blocks");

    public static final ResourceKey<CreativeModeTab> COLORED_BLOCKS_KEY = createResourceKey("colored_blocks");

    public static final ResourceKey<CreativeModeTab> COMBAT_KEY = createResourceKey("combat");

    public static final ResourceKey<CreativeModeTab> INGREDIENTS_KEY = createResourceKey("ingredients");

    public static final ResourceKey<CreativeModeTab> SPAWN_EGGS_KEY = createResourceKey("spawn_eggs");

    public static final ResourceKey<CreativeModeTab> TOOLS_AND_UTILITIES_KEY = createResourceKey("tools_and_utilities");

    public static final CreativeModeTab BLOCKS = register(BLOCKS_KEY, () -> new ItemStack(AVPBlocks.RESIN));

    public static final CreativeModeTab COLORED_BLOCKS = register(
        COLORED_BLOCKS_KEY,
        () -> new ItemStack(AVPBlocks.DYE_COLOR_TO_PLASTIC.get(DyeColor.WHITE))
    );

    public static final CreativeModeTab COMBAT = register(COMBAT_KEY, () -> new ItemStack(ArmorItems.CHITIN_HELMET));

    public static final CreativeModeTab INGREDIENTS = register(INGREDIENTS_KEY, () -> new ItemStack(AVPItems.PLATED_CHITIN));

    public static final CreativeModeTab SPAWN_EGGS = register(SPAWN_EGGS_KEY, () -> new ItemStack(SpawnEggItems.OVAMORPH_SPAWN_EGG));

    public static final CreativeModeTab TOOLS_AND_UTILITIES = register(
        TOOLS_AND_UTILITIES_KEY,
        () -> new ItemStack(AVPItems.ROYAL_JELLY_CANISTER)
    );

    public static ResourceKey<CreativeModeTab> createResourceKey(String name) {
        return ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(),
            AVPResources.location(name)
        );
    }

    public static CreativeModeTab register(ResourceKey<CreativeModeTab> resourceKey, Supplier<ItemStack> iconSupplier) {
        var path = resourceKey.location().getPath();

        var creativeModeTab = FabricItemGroup.builder()
            .icon(iconSupplier)
            .title(Component.translatable(BASE_PATH + "." + AVP.MOD_ID + "." + path))
            .build();

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, resourceKey, creativeModeTab);

        return creativeModeTab;
    }
}
