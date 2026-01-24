package com.blib.fabric.internal.data.lang.en_us.provider;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

import com.blib.api.common.tag.v1.BLibEntityTypeTags;

@ApiStatus.Internal
public final class EnUsEntityTypeTagProvider {

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        builder.add(BLibEntityTypeTags.HUMANOIDS, "Humanoids");
        builder.add(BLibEntityTypeTags.NETHER_CREATURES, "Nether Creatures");
    };
}
