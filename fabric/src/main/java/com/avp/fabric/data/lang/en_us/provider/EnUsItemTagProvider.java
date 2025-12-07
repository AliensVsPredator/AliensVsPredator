package com.avp.fabric.data.lang.en_us.provider;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.util.function.Consumer;

import com.blib.common.registry.tag.BLibItemTags;

public class EnUsItemTagProvider {

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        builder.add(BLibItemTags.DECORATIVE_POT_SHERDS, "Decorative Pot Sherds");
        builder.add(BLibItemTags.FIRE_RESISTANT_ARMORS, "Fire-Resistant Armors");
        builder.add(BLibItemTags.IRON_BLOCK_LIKE, "Iron Block Like");
        builder.add(BLibItemTags.IRON_INGOT_LIKE, "Iron Ingot Like");
        builder.add(BLibItemTags.MELEE_WEAPONS, "Melee Weapons");
        builder.add(BLibItemTags.RANGED_WEAPONS, "Ranged Weapons");
    };
}
