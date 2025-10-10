package com.avp.fabric.data.lang.en_us.provider;

import com.compat.CommonItemTags;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.util.function.Consumer;

import com.avp.common.registry.tag.AVPItemTags;

public class EnUsItemTagProvider {

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        builder.add(AVPItemTags.ABERRANT_CHITIN_ARMOR, "Aberrant Chitin Armor");
        builder.add(AVPItemTags.ACID_IMMUNE, "Acid Immune");
        builder.add(AVPItemTags.AMMO_ITEMS, "Ammo Items");
        builder.add(AVPItemTags.CHITIN_ARMORS, "Chitin Armors");
        builder.add(AVPItemTags.DECORATIVE_POT_SHERDS, "Decorative Pot Sherds");
        builder.add(AVPItemTags.FACEHUGGER_RESISTANT_HELMETS, "Facehugger-Resistant Helmets");
        builder.add(AVPItemTags.FIRE_RESISTANT_ARMORS, "Fire-Resistant Armors");
        builder.add(AVPItemTags.GUNS, "Guns");
        builder.add(AVPItemTags.HOSTILE_WEAPONS, "Hostile Weapons");
        builder.add(AVPItemTags.INDUSTRIAL_GLASS, "Industrial Glass");
        builder.add(AVPItemTags.INDUSTRIAL_GLASS_BLOCK, "Industrial Glass Blocks");
        builder.add(AVPItemTags.INDUSTRIAL_GLASS_PANE, "Industrial Glass Panes");
        builder.add(AVPItemTags.IRON_BLOCK_LIKE, "Iron Block Like");
        builder.add(AVPItemTags.IRON_INGOT_LIKE, "Iron Ingot Like");
        builder.add(AVPItemTags.IRRADIATED_CHITIN_ARMOR, "Irradiated Chitin Armor");
        builder.add(AVPItemTags.JUNGLE_PREDATOR_ARMOR, "Jungle Predator Armor");
        builder.add(AVPItemTags.LITHIUM, "Lithium");
        builder.add(AVPItemTags.MELEE_WEAPONS, "Melee Weapons");
        builder.add(AVPItemTags.MK50_ARMOR, "MK50 Armor");
        builder.add(AVPItemTags.NETHER_CHITIN_ARMOR, "Nether Chitin Armor");
        builder.add(AVPItemTags.NORMAL_CHITIN_ARMOR, "Chitin Armor");
        builder.add(AVPItemTags.PLATED_ABERRANT_CHITIN_ARMOR, "Plated Aberrant Chitin Armor");
        builder.add(AVPItemTags.PLATED_CHITIN_ARMORS, "Plated Chitin Armors");
        builder.add(AVPItemTags.PLATED_IRRADIATED_CHITIN_ARMOR, "Plated Irradiated Chitin Armor");
        builder.add(AVPItemTags.PLATED_NETHER_CHITIN_ARMOR, "Plated Nether Chitin Armor");
        builder.add(AVPItemTags.PLATED_NORMAL_CHITIN_ARMOR, "Plated Normal Chitin Armor");
        builder.add(AVPItemTags.PREDATOR_ARMORS, "Predator Armors");
        builder.add(AVPItemTags.PRESSURE_ARMOR, "Pressure Armor");
        builder.add(AVPItemTags.RADIATION_CURE_ITEMS, "Radiation Cure Items");
        builder.add(AVPItemTags.RADIATION_RESISTANT_ARMORS, "Radiation-Resistant Armors");
        builder.add(AVPItemTags.RADIOACTIVE_ITEMS, "Radioactive Items");
        builder.add(AVPItemTags.RANGED_WEAPONS, "Ranged Weapons");
        builder.add(AVPItemTags.URANIUM_NUGGET_LIKE, "Uranium Nugget Like");

        // Common Tags
        builder.add(CommonItemTags.HEAVY_METAL, "Heavy Metals");
        builder.add(CommonItemTags.HIDDEN_FROM_RECIPE_VIEWERS, "Hidden From Recipe Viewers");
        builder.add(CommonItemTags.INGOTS, "Ingots");
        builder.add(CommonItemTags.INGOTS_ALUMINUM, "Aluminum Ingots");
        builder.add(CommonItemTags.INGOTS_BRASS, "Brass Ingots");
        builder.add(CommonItemTags.INGOTS_LEAD, "Lead Ingots");
        builder.add(CommonItemTags.INGOTS_STEEL, "Steel Ingots");
        builder.add(CommonItemTags.INGOTS_TITANIUM, "Titanium Ingots");
        builder.add(CommonItemTags.INGOTS_URANIUM, "Uranium Ingots");
        builder.add(CommonItemTags.INGOTS_ZINC, "Zinc Ingots");

        builder.add(CommonItemTags.NUGGETS, "Nuggets");
        builder.add(CommonItemTags.NUGGETS_ALUMINUM, "Aluminum Nuggets");
        builder.add(CommonItemTags.NUGGETS_BRASS, "Brass Nuggets");
        builder.add(CommonItemTags.NUGGETS_LEAD, "Lead Nuggets");
        builder.add(CommonItemTags.NUGGETS_STEEL, "Steel Nuggets");
        builder.add(CommonItemTags.NUGGETS_TITANIUM, "Titanium Nuggets");
        builder.add(CommonItemTags.NUGGETS_ZINC, "Zinc Nuggets");
        builder.add(CommonItemTags.ORES, "Ores");
    };
}
