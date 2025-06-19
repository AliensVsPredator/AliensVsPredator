package com.avp.fabric.data.lang.en_us.provider;

import com.alien.common.data.AlienAdvancements;
import com.lib.common.data.AdvancementAccess;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.util.function.Consumer;

public class EnUsAdvancementProvider {

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        addAdvancement(
            builder,
            AlienAdvancements.ROOT,
            "AVP: Aliens",
            "In Minecraft, no one can hear you scream"
        );

        addAdvancement(
            builder,
            AlienAdvancements.KILL_A_HIVE,
            "Hive Buster",
            "Kill an alien hive"
        );

        addAdvancement(
            builder,
            AlienAdvancements.KILL_A_ROYAL_ALIEN,
            "Regicide",
            "Kill a royal alien"
        );

        addAdvancement(
            builder,
            AlienAdvancements.KILL_ALL_ALIENS,
            "Xenocide",
            "Kill one of every alien"
        );

        addAdvancement(
            builder,
            AlienAdvancements.KILL_AN_ALIEN,
            "Imperfect Organism",
            "Kill an alien and live to tell the tale"
        );

        addAdvancement(
            builder,
            AlienAdvancements.WEAR_CHITIN_ARMOR,
            "Cover Me with... Uh...",
            "Equip a full set of chitin armor"
        );

        addAdvancement(
            builder,
            AlienAdvancements.SHEAR_AN_OVOMORPH,
            "Eggsploration Time",
            "Free an ovomorph from its bindings"
        );

        addAdvancement(
            builder,
            AlienAdvancements.WEAR_PLATED_CHITIN_ARMOR,
            "Kneel to the Crown",
            "Equip a full set of plated chitin armor"
        );

        addAdvancement(
            builder,
            AlienAdvancements.REMOVE_EMBRYO_WITH_CHORUS_FRUIT,
            "Eviction",
            "Remove an alien from your chest by eating chorus fruit"
        );
    };

    private static void addAdvancement(
        FabricLanguageProvider.TranslationBuilder builder,
        AdvancementAccess advancementAccess,
        String title,
        String description
    ) {
        builder.add(advancementAccess.getTitleTranslationKey(), title);
        builder.add(advancementAccess.getDescriptionTranslationKey(), description);
    }
}
