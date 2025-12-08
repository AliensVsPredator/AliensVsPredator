package com.blib.fabric.data.lang.en_us.provider;

import com.blib.common.data.tag.BLibBlockTags;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.util.function.Consumer;

public class EnUsBlockTagProvider {

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        builder.add(BLibBlockTags.SHOULD_NOT_BE_DESTROYED, "Should Not Be Destroyed");
    };
}
