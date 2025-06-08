package com.avp.fabric.data.lang.en_us;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.util.function.Consumer;

public class EnUsKeybindProvider {

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        builder.add("key.avp.crawl", "Crawl");
        builder.add("key.avp.reload", "Reload");
        builder.add("keybind.category.avp.movement", "AVP Movement");
        builder.add("keybind.category.avp.weapons", "AVP Weapons");
    };
}
