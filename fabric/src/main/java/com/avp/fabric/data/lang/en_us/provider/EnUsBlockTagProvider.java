package com.avp.fabric.data.lang.en_us.provider;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.util.function.Consumer;

import com.avp.common.registry.tag.AVPBlockTags;

public class EnUsBlockTagProvider {

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        builder.add(AVPBlockTags.CONCRETE, "Concrete");
        builder.add(AVPBlockTags.FERROALUMINUM, "Ferroaluminum");
        builder.add(AVPBlockTags.INDUSTRIAL_CONCRETE, "Industrial Concrete");
        builder.add(AVPBlockTags.INDUSTRIAL_GLASS, "Industrial Glass");
        builder.add(AVPBlockTags.INDUSTRIAL_GLASS_BLOCK, "Industrial Glass Blocks");
        builder.add(AVPBlockTags.INDUSTRIAL_GLASS_PANE, "Industrial Glass Panes");
        builder.add(AVPBlockTags.MARINE_SPAWN_BLOCKS, "Marine Spawn Blocks");
        builder.add(AVPBlockTags.PADDING, "Padding");
        builder.add(AVPBlockTags.PLASTIC, "Plastic");
        builder.add(AVPBlockTags.RAZOR_WIRE, "Razor Wire");
        builder.add(AVPBlockTags.SHOULD_NOT_BE_DESTROYED, "Should Not Be Destroyed");
        builder.add(AVPBlockTags.STEEL, "Steel");
        builder.add(AVPBlockTags.TITANIUM, "Titanium");
    };
}
