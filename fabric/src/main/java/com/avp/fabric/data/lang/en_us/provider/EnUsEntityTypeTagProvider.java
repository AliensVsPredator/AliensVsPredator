package com.avp.fabric.data.lang.en_us.provider;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.util.function.Consumer;

import com.avp.common.registry.tag.AVPEntityTypeTags;

public class EnUsEntityTypeTagProvider {

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        builder.add(AVPEntityTypeTags.HUMANOIDS, "Humanoids");
        builder.add(AVPEntityTypeTags.NETHER_CREATURES, "Nether Creatures");
        builder.add(AVPEntityTypeTags.RADIATION_RESISTANT, "Radiation Resistant");
    };
}
