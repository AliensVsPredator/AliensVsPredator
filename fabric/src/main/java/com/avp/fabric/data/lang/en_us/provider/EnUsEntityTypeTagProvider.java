package com.avp.fabric.data.lang.en_us.provider;

import com.blib.common.registry.tag.BLibEntityTypeTags;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.util.function.Consumer;

public class EnUsEntityTypeTagProvider {

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        builder.add(BLibEntityTypeTags.HUMANOIDS, "Humanoids");
        builder.add(BLibEntityTypeTags.NETHER_CREATURES, "Nether Creatures");
    };
}
