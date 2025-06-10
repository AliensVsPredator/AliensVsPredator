package com.avp.fabric.data.lang.en_us.provider;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.util.function.Consumer;

import com.avp.common.registry.key.AVPCreativeModeTabKeys;

public class EnUsCreativeModeTabProvider {

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        builder.add(AVPCreativeModeTabKeys.BLOCKS_KEY, "AVP Blocks");
        builder.add(AVPCreativeModeTabKeys.COLORED_BLOCKS_KEY, "AVP Colored Blocks");
        builder.add(AVPCreativeModeTabKeys.COMBAT_KEY, "AVP Combat");
        builder.add(AVPCreativeModeTabKeys.INGREDIENTS_KEY, "AVP Ingredients");
        builder.add(AVPCreativeModeTabKeys.SPAWN_EGGS_KEY, "AVP Spawn Eggs");
        builder.add(AVPCreativeModeTabKeys.TOOLS_AND_UTILITIES_KEY, "AVP Tools & Utilities");
    };
}
