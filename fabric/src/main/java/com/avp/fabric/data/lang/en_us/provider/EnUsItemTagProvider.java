package com.avp.fabric.data.lang.en_us.provider;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.util.function.Consumer;

import com.avp.common.registry.tag.AVPItemTags;

public class EnUsItemTagProvider {

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        builder.add(AVPItemTags.DECORATIVE_POT_SHERDS, "Decorative Pot Sherds");
        builder.add(AVPItemTags.FIRE_RESISTANT_ARMORS, "Fire-Resistant Armors");
        builder.add(AVPItemTags.IRON_BLOCK_LIKE, "Iron Block Like");
        builder.add(AVPItemTags.IRON_INGOT_LIKE, "Iron Ingot Like");
        builder.add(AVPItemTags.MELEE_WEAPONS, "Melee Weapons");
        builder.add(AVPItemTags.RANGED_WEAPONS, "Ranged Weapons");
    };
}
