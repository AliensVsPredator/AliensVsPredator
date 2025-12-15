package com.blib.fabric.internal.data.lang.en_us.provider;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

import com.blib.common.data.tag.BLibBlockTags;

@ApiStatus.Internal
public final class EnUsBlockTagProvider {

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        builder.add(BLibBlockTags.SHOULD_NOT_BE_DESTROYED, "Should Not Be Destroyed");
    };
}
