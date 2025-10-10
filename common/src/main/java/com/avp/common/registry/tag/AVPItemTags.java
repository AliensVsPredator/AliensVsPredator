package com.avp.common.registry.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import com.avp.AVPResources;

public class AVPItemTags {

    public static final TagKey<Item> ABERRANT_CHITIN_ARMOR = create("aberrant_chitin_armor");

    public static final TagKey<Item> ACID_IMMUNE = create("acid_immune");

    public static final TagKey<Item> AMMO_ITEMS = create("ammo_items");

    public static final TagKey<Item> CHITIN_ARMORS = create("chitin_armors");

    public static final TagKey<Item> DECORATIVE_POT_SHERDS = create("decorative_pot_sherds");

    public static final TagKey<Item> FACEHUGGER_RESISTANT_HELMETS = create("facehugger_resistant_helmets");

    public static final TagKey<Item> FIRE_RESISTANT_ARMORS = create("fire_resistant_armors");

    public static final TagKey<Item> GUNS = create("guns");

    public static final TagKey<Item> HOSTILE_WEAPONS = create("hostile_weapons");

    public static final TagKey<Item> INDUSTRIAL_GLASS = create("industrial_glass");

    public static final TagKey<Item> INDUSTRIAL_GLASS_BLOCK = create("industrial_glass_block");

    public static final TagKey<Item> INDUSTRIAL_GLASS_PANE = create("industrial_glass_pane");

    public static final TagKey<Item> IRON_BLOCK_LIKE = create("iron_block_like");

    public static final TagKey<Item> IRON_INGOT_LIKE = create("iron_ingot_like");

    public static final TagKey<Item> IRRADIATED_CHITIN_ARMOR = create("irradiated_chitin_armor");

    public static final TagKey<Item> JUNGLE_PREDATOR_ARMOR = create("jungle_predator_armor");

    public static final TagKey<Item> LITHIUM = create("lithium");

    public static final TagKey<Item> MELEE_WEAPONS = create("melee_weapons");

    public static final TagKey<Item> MK50_ARMOR = create("mk50_armor");

    public static final TagKey<Item> NETHER_CHITIN_ARMOR = create("nether_chitin_armor");

    public static final TagKey<Item> NORMAL_CHITIN_ARMOR = create("normal_chitin_armor");

    public static final TagKey<Item> PLASTIC = create("plastic");

    public static final TagKey<Item> PLATED_ABERRANT_CHITIN_ARMOR = create("plated_aberrant_chitin_armor");

    public static final TagKey<Item> PLATED_CHITIN_ARMORS = create("plated_chitin_armors");

    public static final TagKey<Item> PLATED_IRRADIATED_CHITIN_ARMOR = create("plated_irradiated_chitin_armor");

    public static final TagKey<Item> PLATED_NETHER_CHITIN_ARMOR = create("plated_nether_chitin_armor");

    public static final TagKey<Item> PLATED_NORMAL_CHITIN_ARMOR = create("plated_normal_chitin_armor");

    public static final TagKey<Item> PREDATOR_ARMORS = create("predator_armors");

    public static final TagKey<Item> PRESSURE_ARMOR = create("pressure_armor");

    public static final TagKey<Item> RADIATION_CURE_ITEMS = create("radiation_cure_items");

    public static final TagKey<Item> RADIATION_RESISTANT_ARMORS = create("radiation_resistant_armors");

    public static final TagKey<Item> RADIOACTIVE_ITEMS = create("radioactive_items");

    public static final TagKey<Item> RANGED_WEAPONS = create("ranged_weapons");

    public static final TagKey<Item> URANIUM_NUGGET_LIKE = create("uranium_nugget_like");

    private static TagKey<Item> create(String name) {
        return TagKey.create(Registries.ITEM, AVPResources.location(name));
    }
}
