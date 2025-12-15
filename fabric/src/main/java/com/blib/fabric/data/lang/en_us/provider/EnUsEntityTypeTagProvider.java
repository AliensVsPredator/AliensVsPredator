package com.blib.fabric.data.lang.en_us.provider;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.util.function.Consumer;

import com.blib.common.data.tag.BLibEntityTypeTags;

public class EnUsEntityTypeTagProvider {

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        builder.add(BLibEntityTypeTags.HUMANOIDS, "Humanoids");
        builder.add(BLibEntityTypeTags.NETHER_CREATURES, "Nether Creatures");
    };
}
