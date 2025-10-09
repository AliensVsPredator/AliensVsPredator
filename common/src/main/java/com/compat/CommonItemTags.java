package com.compat;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class CommonItemTags {

    public static final TagKey<Item> DIAMONDS = create("diamonds");

    public static final TagKey<Item> DUSTS_AUTUNITE = create("dusts/autunite");

    public static final TagKey<Item> DUSTS_COAL = create("dusts/coal");

    public static final TagKey<Item> DUSTS_REDSTONE = create("dusts/redstone");

    public static final TagKey<Item> GEMS_DIAMOND = create("gems/diamond");

    public static final TagKey<Item> HEAVY_METAL = create("ingots/heavy_metal");

    public static final TagKey<Item> HIDDEN_FROM_RECIPE_VIEWERS = create("hidden_from_recipe_viewers");

    public static final TagKey<Item> INGOTS = create("ingots");

    public static final TagKey<Item> INGOTS_ALUMINUM = create("ingots/aluminum");

    public static final TagKey<Item> INGOTS_BRASS = create("ingots/brass");

    public static final TagKey<Item> INGOTS_FERROALUMINUM = create("ingots/ferroaluminum");

    public static final TagKey<Item> INGOTS_LEAD = create("ingots/lead");

    public static final TagKey<Item> INGOTS_STEEL = create("ingots/steel");

    public static final TagKey<Item> INGOTS_TITANIUM = create("ingots/titanium");

    public static final TagKey<Item> INGOTS_URANIUM = create("ingots/uranium");

    public static final TagKey<Item> INGOTS_ZINC = create("ingots/zinc");

    public static final TagKey<Item> LEATHERS = create("leathers");

    public static final TagKey<Item> MUSIC_DISCS = create("music_discs");

    public static final TagKey<Item> NUGGETS = create("nuggets");

    public static final TagKey<Item> NUGGETS_ALUMINUM = create("nuggets/aluminum");

    public static final TagKey<Item> NUGGETS_BRASS = create("nuggets/brass");

    public static final TagKey<Item> NUGGETS_FERROALUMINUM = create("nuggets/ferroaluminum");

    public static final TagKey<Item> NUGGETS_GOLD = create("nuggets/gold");

    public static final TagKey<Item> NUGGETS_IRON = create("nuggets/iron");

    public static final TagKey<Item> NUGGETS_LEAD = create("nuggets/lead");

    public static final TagKey<Item> NUGGETS_STEEL = create("nuggets/steel");

    public static final TagKey<Item> NUGGETS_TITANIUM = create("nuggets/titanium");

    public static final TagKey<Item> NUGGETS_URANIUM = create("nuggets/uranium");

    public static final TagKey<Item> NUGGETS_ZINC = create("nuggets/zinc");

    public static final TagKey<Item> ORES = create("ores");

    public static final TagKey<Item> ORES_ALUMINUM = create("ores/aluminum");

    public static final TagKey<Item> ORES_AUTUNITE = create("ores/autunite");

    public static final TagKey<Item> ORES_BAUXITE = create("ores/bauxite");

    public static final TagKey<Item> ORES_GALENA = create("ores/galena");

    public static final TagKey<Item> ORES_LEAD = create("ores/lead");

    public static final TagKey<Item> ORES_LITHIUM = create("ores/lithium");

    public static final TagKey<Item> ORES_MONAZITE = create("ores/monazite");

    public static final TagKey<Item> ORES_TITANIUM = create("ores/titanium");

    public static final TagKey<Item> ORES_ZINC = create("ores/zinc");

    public static final TagKey<Item> RAW_MATERIALS = create("raw_materials");

    public static final TagKey<Item> RAW_MATERIALS_ALUMINUM = create("raw_materials/aluminum");

    public static final TagKey<Item> RAW_MATERIALS_COPPER = create("raw_materials/copper");

    public static final TagKey<Item> RAW_MATERIALS_IRON = create("raw_materials/iron");

    public static final TagKey<Item> RAW_MATERIALS_LEAD = create("raw_materials/lead");

    public static final TagKey<Item> RAW_MATERIALS_STEEL = create("raw_materials/steel");

    public static final TagKey<Item> RAW_MATERIALS_TITANIUM = create("raw_materials/titanium");

    public static final TagKey<Item> RAW_MATERIALS_ZINC = create("raw_materials/zinc");

    public static final TagKey<Item> SILICON = create("silicon");

    public static final TagKey<Item> STORAGE_BLOCKS = create("storage_blocks");

    public static final TagKey<Item> STORAGE_BLOCKS_ALUMINUM = create("storage_blocks/aluminum");

    public static final TagKey<Item> STORAGE_BLOCKS_BRASS = create("storage_blocks/brass");

    public static final TagKey<Item> STORAGE_BLOCKS_FERROALUMINUM = create("storage_blocks/ferroaluminum");

    public static final TagKey<Item> STORAGE_BLOCKS_LEAD = create("storage_blocks/lead");

    public static final TagKey<Item> STORAGE_BLOCKS_RAW_ALUMINUM = create("storage_blocks/raw_aluminum");

    public static final TagKey<Item> STORAGE_BLOCKS_RAW_LEAD = create("storage_blocks/raw_lead");

    public static final TagKey<Item> STORAGE_BLOCKS_RAW_TITANIUM = create("storage_blocks/raw_titanium");

    public static final TagKey<Item> STORAGE_BLOCKS_RAW_ZINC = create("storage_blocks/raw_zinc");

    public static final TagKey<Item> STORAGE_BLOCKS_STEEL = create("storage_blocks/steel");

    public static final TagKey<Item> STORAGE_BLOCKS_TITANIUM = create("storage_blocks/titanium");

    public static final TagKey<Item> STORAGE_BLOCKS_URANIUM = create("storage_blocks/uranium");

    public static final TagKey<Item> STORAGE_BLOCKS_ZINC = create("storage_blocks/zinc");

    private static TagKey<Item> create(String name) {
        return TagKey.create(Registries.ITEM, CommonConstants.location(name));
    }

}
