package com.avp.fabric.data.lang.en_us;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.util.function.Consumer;

import com.avp.common.registry.tag.AVPBiomeTags;

public class EnUsBiomeTagProvider {

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        builder.add(AVPBiomeTags.HAS_ALTAR, "Has Altar");
        builder.add(AVPBiomeTags.HAS_BADLANDS_ALTAR, "Has Badlands Altar");
        builder.add(AVPBiomeTags.HAS_DESERT_ALTAR, "Has Desert Altar");
        builder.add(AVPBiomeTags.HAS_DEEPSLATE_ALTAR, "Has Deepslate Altar");
        builder.add(AVPBiomeTags.HAS_JUNGLE_ALTAR, "Has Jungle Altar");
        builder.add(AVPBiomeTags.HAS_NETHER_ALTAR, "Has Nether Altar");
        builder.add(AVPBiomeTags.HAS_MARINE_CAMP_GRASS, "Has Grassy Marine Camp");
        builder.add(AVPBiomeTags.HAS_MOBILE_LAB, "Has Mobile Lab");
        builder.add(AVPBiomeTags.HAS_OUTPOST_COMMS, "Has Communications Outpost");
        builder.add(AVPBiomeTags.HAS_OUTPOST_MUNITION, "Has Munitions Outpost");
        builder.add(AVPBiomeTags.HAS_OUTPOST_SUPPLY_BADLAND, "Has Badlands Supply Outpost");
        builder.add(AVPBiomeTags.HAS_OUTPOST_SUPPLY_DESERT, "Has Desert Supply Outpost");
        builder.add(AVPBiomeTags.HAS_XENOMORPHS, "Has Xenomorphs");
        builder.add(AVPBiomeTags.IS_IRRADIATED, "Is Irradiated");
    };
}
