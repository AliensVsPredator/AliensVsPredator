package com.avp.fabric.data.lang.en_us.provider;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.util.function.Consumer;

import com.avp.common.registry.tag.AVPMobEffectTags;

public class EnUsMobEffectTagProvider {

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        builder.add(AVPMobEffectTags.DOES_NOT_AFFECT_ALIENS, "Does Not Affect Aliens");
    };
}
