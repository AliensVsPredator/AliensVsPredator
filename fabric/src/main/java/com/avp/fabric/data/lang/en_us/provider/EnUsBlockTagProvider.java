package com.avp.fabric.data.lang.en_us.provider;

import com.avp.common.registry.tag.AVPBlockTags;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.util.function.Consumer;

public class EnUsBlockTagProvider {

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        builder.add(AVPBlockTags.SHOULD_NOT_BE_DESTROYED, "Should Not Be Destroyed");
    };
}
