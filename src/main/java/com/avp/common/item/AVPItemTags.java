package com.avp.common.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import com.avp.AVPResources;

public class AVPItemTags {

    public static final TagKey<Item> HOSTILE_WEAPON = create("hostile_weapon");

    public static final TagKey<Item> ACID_IMMUNE = create("acid_immune");

    public static final TagKey<Item> DECORATIVE_POT_SHERDS = create("decorative_pot_sherds");

    public static final TagKey<Item> GUNS = create("guns");

    public static final TagKey<Item> INDUSTRIAL_GLASS = create("industrial_glass");

    public static final TagKey<Item> INDUSTRIAL_GLASS_BLOCK = create("industrial_glass_block");

    public static final TagKey<Item> INDUSTRIAL_GLASS_PANE = create("industrial_glass_pane");

    public static final TagKey<Item> IRON_BLOCK_LIKE = create("iron_block_like");

    public static final TagKey<Item> IRON_INGOT_LIKE = create("iron_ingot_like");

    public static final TagKey<Item> LITHIUM = create("lithium");

    public static final TagKey<Item> RADIATION_RESISTANT_ARMOR = create("radiation_resistant_armor");

    public static final TagKey<Item> RADIATION_CURE_ITEMS = create("radiation_cure_items");

    public static final TagKey<Item> RADIATION_ITEMS = create("radiation_items");

    public static final TagKey<Item> AMMO_ITEMS = create("ammo_items");

    private static TagKey<Item> create(String name) {
        return TagKey.create(Registries.ITEM, AVPResources.location(name));
    }
}
