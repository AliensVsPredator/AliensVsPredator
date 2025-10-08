package com.compat;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class CommonConstants {

    public static final String MOD_ID = "c";

    /*
     * Items
     */
    public static final TagKey<Item> DUSTS_REDSTONE = createItem("dusts/redstone");

    public static final TagKey<Item> HEAVY_METAL = createItem("ingots/heavy_metal");

    public static final TagKey<Item> HIDDEN_FROM_RECIPE_VIEWERS = createItem("hidden_from_recipe_viewers");

    public static final TagKey<Item> INGOTS = createItem("ingots");

    public static final TagKey<Item> INGOTS_ALUMINUM = createItem("ingots/aluminum");

    public static final TagKey<Item> INGOTS_BRASS = createItem("ingots/brass");

    public static final TagKey<Item> INGOTS_FERROALUMINUM = createItem("ingots/ferroaluminum");

    public static final TagKey<Item> INGOTS_LEAD = createItem("ingots/lead");

    public static final TagKey<Item> INGOTS_STEEL = createItem("ingots/steel");

    public static final TagKey<Item> INGOTS_TITANIUM = createItem("ingots/titanium");

    public static final TagKey<Item> INGOTS_URANIUM = createItem("ingots/uranium");

    public static final TagKey<Item> INGOTS_ZINC = createItem("ingots/zinc");

    public static final TagKey<Item> NUGGETS = createItem("nuggets");

    public static final TagKey<Item> NUGGETS_ALUMINUM = createItem("nuggets/aluminum");

    public static final TagKey<Item> NUGGETS_BRASS = createItem("nuggets/brass");

    public static final TagKey<Item> NUGGETS_FERROALUMINUM = createItem("nuggets/ferroaluminum");

    public static final TagKey<Item> NUGGETS_GOLD = createItem("nuggets/gold");

    public static final TagKey<Item> NUGGETS_IRON = createItem("nuggets/iron");

    public static final TagKey<Item> NUGGETS_LEAD = createItem("nuggets/lead");

    public static final TagKey<Item> NUGGETS_STEEL = createItem("nuggets/steel");

    public static final TagKey<Item> NUGGETS_TITANIUM = createItem("nuggets/titanium");

    public static final TagKey<Item> NUGGETS_URANIUM = createItem("nuggets/uranium");

    public static final TagKey<Item> NUGGETS_ZINC = createItem("nuggets/zinc");

    public static final TagKey<Item> ORES = createItem("ores");

    public static final TagKey<Item> RAW_MATERIALS_ALUMINUM = createItem("raw_materials/aluminum");

    public static final TagKey<Item> RAW_MATERIALS_COPPER = createItem("raw_materials/copper");

    public static final TagKey<Item> RAW_MATERIALS_IRON = createItem("raw_materials/iron");

    public static final TagKey<Item> RAW_MATERIALS_LEAD = createItem("raw_materials/lead");

    public static final TagKey<Item> RAW_MATERIALS_TITANIUM = createItem("raw_materials/titanium");

    public static final TagKey<Item> RAW_MATERIALS_ZINC = createItem("raw_materials/zinc");

    public static final TagKey<Item> SILICON = createItem("silicon");

    /*
     * Blocks
     */
    public static final TagKey<Block> CHESTS = createBlock("chests");

    public static final TagKey<Block> DYED = createBlock("dyed");

    public static final TagKey<Block> GLASS_BLOCKS = createBlock("glass_blocks");

    public static final TagKey<Block> ORES_BLOCKS = createBlock("ores");

    private static TagKey<Block> createBlock(String name) {
        return TagKey.create(Registries.BLOCK, location(name));
    }

    private static TagKey<Item> createItem(String name) {
        return TagKey.create(Registries.ITEM, location(name));
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
