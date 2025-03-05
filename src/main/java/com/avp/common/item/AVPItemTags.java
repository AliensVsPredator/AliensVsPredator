package com.avp.common.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import com.avp.AVPResources;

public class AVPItemTags {

    public static final TagKey<Item> ACID_IMMUNE = create("acid_immune");

    public static final TagKey<Item> DECORATIVE_POT_SHERDS = create("decorative_pot_sherds");

    public static final TagKey<Item> GUNS = create("guns");

    public static final TagKey<Item> INDUSTRIAL_GLASS = create("industrial_glass");

    public static final TagKey<Item> INDUSTRIAL_GLASS_BLOCK = create("industrial_glass_block");

    public static final TagKey<Item> INDUSTRIAL_GLASS_PANE = create("industrial_glass_pane");

    public static final TagKey<Item> IRON_BLOCK_LIKE = create("iron_block_like");

    public static final TagKey<Item> IRON_INGOT_LIKE = create("iron_ingot_like");

    public static final TagKey<Item> LITHIUM = create("lithium");

    private static TagKey<Item> create(String name) {
        return TagKey.create(Registries.ITEM, AVPResources.location(name));
    }
}
