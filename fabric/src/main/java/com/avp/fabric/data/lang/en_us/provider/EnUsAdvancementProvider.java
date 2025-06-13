package com.avp.fabric.data.lang.en_us.provider;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.util.function.Consumer;

public class EnUsAdvancementProvider {

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        builder.add("advancements.aliens.root.title", "AVP: Aliens");
        builder.add("advancements.aliens.root.description", "In Minecraft, no one can hear you scream");

        builder.add("advancements.aliens.kill_an_alien.title", "Imperfect Organism");
        builder.add("advancements.aliens.kill_an_alien.description", "Kill an alien and live to tell the tale");

        builder.add("advancements.aliens.kill_a_royal_alien.title", "Regicide");
        builder.add("advancements.aliens.kill_a_royal_alien.description", "Kill a royal alien");

        builder.add("advancements.aliens.kill_all_aliens.title", "Xenocide");
        builder.add("advancements.aliens.kill_all_aliens.description", "Kill one of every alien");

        builder.add("advancements.aliens.chitin_armor.title", "Cover Me with... Uh...");
        builder.add("advancements.aliens.chitin_armor.description", "Equip a full set of chitin armor");

        builder.add("advancements.aliens.shear_an_ovomorph.title", "Eggsploration Time");
        builder.add("advancements.aliens.shear_an_ovomorph.description", "Free an ovomorph from its bindings");

        builder.add("advancements.aliens.plated_chitin_armor.title", "Kneel to the Crown");
        builder.add("advancements.aliens.plated_chitin_armor.description", "Equip a full set of plated chitin armor");
    };
}
