package com.avp.fabric.data.lang.en_us;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.util.function.Consumer;

import com.avp.common.registry.tag.AVPBlockTags;

public class EnUsBlockTagProvider {

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        builder.add(AVPBlockTags.ABERRANT_CHITIN, "Aberrant Chitin");
        builder.add(AVPBlockTags.ABERRANT_RESIN, "Aberrant Resin");
        builder.add(AVPBlockTags.ABERRANT_RESIN_REPLACEABLE, "Aberrant Resin Replaceable");
        builder.add(AVPBlockTags.ACID_IMMUNE, "Acid Immune");
        builder.add(AVPBlockTags.CHITIN, "Chitins");
        builder.add(AVPBlockTags.CONCRETE, "Concrete");
        builder.add(AVPBlockTags.FERROALUMINUM, "Ferroaluminum");
        builder.add(AVPBlockTags.INDUSTRIAL_CONCRETE, "Industrial Concrete");
        builder.add(AVPBlockTags.INDUSTRIAL_GLASS, "Industrial Glass");
        builder.add(AVPBlockTags.INDUSTRIAL_GLASS_BLOCK, "Industrial Glass Blocks");
        builder.add(AVPBlockTags.INDUSTRIAL_GLASS_PANE, "Industrial Glass Panes");
        builder.add(AVPBlockTags.IRRADIATED_ACID_IMMUNE, "Irradiated Acid Immune");
        builder.add(AVPBlockTags.IRRADIATED_RESIN, "Irradiated Resin");
        builder.add(AVPBlockTags.IRRADIATED_RESIN_REPLACEABLE, "Irradiated Resin Replaceable");
        builder.add(AVPBlockTags.NETHER_ACID_IMMUNE, "Nether Acid Immune");
        builder.add(AVPBlockTags.NETHER_CHITIN, "Nether Chitin");
        builder.add(AVPBlockTags.NETHER_RESIN, "Nether Resin");
        builder.add(AVPBlockTags.NETHER_RESIN_REPLACEABLE, "Nether Resin Replaceable");
        builder.add(AVPBlockTags.NORMAL_CHITIN, "Chitin");
        builder.add(AVPBlockTags.NORMAL_RESIN, "Resin");
        builder.add(AVPBlockTags.NORMAL_RESIN_REPLACEABLE, "Resin Replaceable");
        builder.add(AVPBlockTags.MARINE_SPAWN_BLOCKS, "Marine Spawn Blocks");
        builder.add(AVPBlockTags.PADDING, "Padding");
        builder.add(AVPBlockTags.PLASTIC, "Plastic");
        builder.add(AVPBlockTags.RAZOR_WIRE, "Razor Wire");
        builder.add(AVPBlockTags.RESIN, "Resins");
        builder.add(AVPBlockTags.RESIN_BLOCKS, "Resin Blocks");
        builder.add(AVPBlockTags.RESIN_NODES, "Resin Nodes");
        builder.add(AVPBlockTags.RESIN_REPLACEABLE, "Resins Replaceable");
        builder.add(AVPBlockTags.RESIN_VEINS, "Resin Veins");
        builder.add(AVPBlockTags.RESIN_WEBS, "Resin Webs");
        builder.add(AVPBlockTags.SHOULD_NOT_BE_DESTROYED, "Should Not Be Destroyed");
        builder.add(AVPBlockTags.STEEL, "Steel");
        builder.add(AVPBlockTags.TITANIUM, "Titanium");
        builder.add(AVPBlockTags.XENOMORPH_IMMUNE, "Xenomorph Immune");
    };
}
