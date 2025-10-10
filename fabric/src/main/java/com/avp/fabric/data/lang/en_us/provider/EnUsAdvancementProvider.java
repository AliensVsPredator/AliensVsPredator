package com.avp.fabric.data.lang.en_us.provider;

import com.alien.common.data.AlienAdvancements;
import com.human.common.data.HumanAdvancements;
import com.lib.common.data.AdvancementAccess;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.util.function.Consumer;

public class EnUsAdvancementProvider {

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        provideAlienAdvancementTranslations(builder);
        provideHumanAdvancementTranslations(builder);
    };

    private static void provideAlienAdvancementTranslations(FabricLanguageProvider.TranslationBuilder builder) {
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
    }

    private static void provideHumanAdvancementTranslations(FabricLanguageProvider.TranslationBuilder builder) {
        addAdvancement(
            builder,
            HumanAdvancements.ROOT,
            "AVP: Humans",
            "Not bad... for a human"
        );

        addAdvancement(
            builder,
            HumanAdvancements.BLAST_STEEL,
            "Steel Yourselves",
            "Blast raw crude iron in a blast furnace"
        );

        addAdvancement(
            builder,
            HumanAdvancements.EQUIP_FULL_ARMOR_SET_WITH_ARMOR_CASE,
            "Hot Swap",
            "Use an armor case to swap a full set of equipped armor with a different armor set"
        );

        addAdvancement(
            builder,
            HumanAdvancements.FILL_CANISTER,
            "Topped Off",
            "Completely fill a canister with a single fluid"
        );

        addAdvancement(
            builder,
            HumanAdvancements.HAS_GUN,
            "Lock & Load",
            "Acquire a gun"
        );

        addAdvancement(
            builder,
            HumanAdvancements.SMELT_BRASS,
            "Brass Ring",
            "Smelt raw brass in a furnace"
        );

        addAdvancement(
            builder,
            HumanAdvancements.SMELT_PLASTIC,
            "It's Fantastic",
            "Smelt slime in a furnace"
        );

        addAdvancement(
            builder,
            HumanAdvancements.SMELT_TITANIUM,
            "I am Titanium",
            "Smelt raw titanium in a furnace"
        );
    }

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
