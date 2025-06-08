package com.avp.fabric.data.lang.en_us;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.util.function.Consumer;

import com.avp.common.registry.tag.AVPDamageTypesTags;

public class EnUsDamageTypeTagProvider {

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        builder.add(AVPDamageTypesTags.DOES_NOT_HURT_SENTRY_TURRETS, "Does Not Hurt Sentry Turrets");
    };
}
