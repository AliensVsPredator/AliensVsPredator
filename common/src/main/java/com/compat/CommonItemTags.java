package com.compat;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class CommonItemTags {

    public static final TagKey<Item> DIAMONDS = create("diamonds");

    public static final TagKey<Item> DUSTS_COAL = create("dusts/coal");

    public static final TagKey<Item> DUSTS_REDSTONE = create("dusts/redstone");

    public static final TagKey<Item> GEMS_DIAMOND = create("gems/diamond");

    public static final TagKey<Item> INGOTS = create("ingots");

    public static final TagKey<Item> INGOTS_COPPER = create("ingots/copper");

    public static final TagKey<Item> INGOTS_GOLD = create("ingots/gold");

    public static final TagKey<Item> NUGGETS = create("nuggets");

    public static final TagKey<Item> NUGGETS_GOLD = create("nuggets/gold");

    public static final TagKey<Item> NUGGETS_IRON = create("nuggets/iron");

    public static final TagKey<Item> RAW_MATERIALS = create("raw_materials");

    public static final TagKey<Item> RAW_MATERIALS_COPPER = create("raw_materials/copper");

    public static final TagKey<Item> RAW_MATERIALS_IRON = create("raw_materials/iron");

    public static final TagKey<Item> RODS_WOODEN = create("rods/wooden");

    public static final TagKey<Item> STRINGS = create("strings");

    private static TagKey<Item> create(String name) {
        return TagKey.create(Registries.ITEM, CommonConstants.location(name));
    }

}
