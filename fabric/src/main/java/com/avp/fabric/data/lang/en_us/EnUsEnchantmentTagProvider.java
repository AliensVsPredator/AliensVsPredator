package com.avp.fabric.data.lang.en_us;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.util.function.Consumer;

import com.avp.common.registry.tag.AVPEnchantmentTags;

public class EnUsEnchantmentTagProvider {

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        builder.add(AVPEnchantmentTags.GUN_ENCHANTMENTS, "Gun Enchantments");
    };
}
