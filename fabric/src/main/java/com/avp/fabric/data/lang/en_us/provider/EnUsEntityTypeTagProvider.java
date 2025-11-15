package com.avp.fabric.data.lang.en_us.provider;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.util.function.Consumer;

import com.avp.common.registry.tag.AVPEntityTypeTags;

public class EnUsEntityTypeTagProvider {

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        builder.add(AVPEntityTypeTags.ABERRANT_ALIENS, "Aberrant Aliens");
        builder.add(AVPEntityTypeTags.ACID_IMMUNE, "Acid Immune");
        builder.add(AVPEntityTypeTags.ALIENS, "Aliens");
        builder.add(AVPEntityTypeTags.CHESTBURSTERS, "Chestbursters");
        builder.add(AVPEntityTypeTags.DRONES, "Drones");
        builder.add(AVPEntityTypeTags.FACEHUGGERS, "Facehuggers");
        builder.add(AVPEntityTypeTags.HATED_BY_XENOMORPHS, "Hated By Xenomorphs");
        builder.add(AVPEntityTypeTags.HIVE_ALIENS, "Hive Aliens");
        builder.add(AVPEntityTypeTags.HOSTS, "Hosts");
        builder.add(AVPEntityTypeTags.HUMANOIDS, "Humanoids");
        builder.add(AVPEntityTypeTags.IRRADIATED_ALIENS, "Irradiated Aliens");
        builder.add(AVPEntityTypeTags.NETHER_ALIENS, "Nether Aliens");
        builder.add(AVPEntityTypeTags.NETHER_CREATURES, "Nether Creatures");
        builder.add(AVPEntityTypeTags.NORMAL_ALIENS, "Aliens");
        builder.add(AVPEntityTypeTags.OVOMORPHS, "Ovomorphs");
        builder.add(AVPEntityTypeTags.PARASITES, "Parasites");
        builder.add(AVPEntityTypeTags.PRAETORIANS, "Praetorians");
        builder.add(AVPEntityTypeTags.PREDALIENS, "Predaliens");
        builder.add(AVPEntityTypeTags.PREDATORS, "Predators");
        builder.add(AVPEntityTypeTags.QUEENS, "Queens");
        builder.add(AVPEntityTypeTags.RADIATION_RESISTANT, "Radiation Resistant");
        builder.add(AVPEntityTypeTags.ROYAL_ALIENS, "Royal Aliens");
        builder.add(AVPEntityTypeTags.ROYAL_XENOMORPHS, "Royal Xenomorphs");
        builder.add(AVPEntityTypeTags.SPAWNS_IN_HIVE_DRONE_LAYER, "Spawns In Hive Drone Layer");
        builder.add(AVPEntityTypeTags.SPAWNS_IN_HIVE_PRAETORIAN_LAYER, "Spawns In Hive Praetorian Layer");
        builder.add(AVPEntityTypeTags.SPAWNS_IN_HIVE_QUEEN_LAYER, "Spawns In Hive Queen Layer");
        builder.add(AVPEntityTypeTags.SPAWNS_IN_HIVE_WARRIOR_LAYER, "Spawns In Hive Warrior Layer");
        builder.add(AVPEntityTypeTags.WARRIORS, "Warriors");
        builder.add(AVPEntityTypeTags.XENOMORPHS, "Xenomorphs");
    };
}
