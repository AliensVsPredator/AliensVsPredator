package com.avp.common.registry.init.creative_mode_tab.initializer;

import com.alien.common.registry.init.item.AlienSpawnEggItems;
import com.human.common.registry.init.item.HumanSpawnEggItems;
import com.predator.common.registry.init.item.PredatorSpawnEggItems;
import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Consumer;

public class SpawnEggsCreativeModeTabInitializer {

    public static final Consumer<CreativeModeTab.Output> OUTPUT_CONSUMER = output -> {
        CreativeModeTabUtil.accept(output, HumanSpawnEggItems.MARINE_SPAWN_EGG);

        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.ADOLESCENT_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.CHESTBURSTER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.CRUSHER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.DRONE_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.FACEHUGGER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.OVOMORPH_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.PRAETORIAN_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.PROWLER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.QUEEN_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.RUNNER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.WARRIOR_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.BOILER_SPAWN_EGG);

        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.NETHER_CHESTBURSTER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.NETHER_ADOLESCENT_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.NETHER_CRUSHER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.NETHER_DRONE_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.NETHER_FACEHUGGER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.NETHER_OVOMORPH_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.NETHER_PRAETORIAN_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.NETHER_PROWLER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.NETHER_RUNNER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.NETHER_WARRIOR_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.NETHER_QUEEN_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.NETHER_BOILER_SPAWN_EGG);

        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.ABERRANT_CHESTBURSTER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.ABERRANT_ADOLESCENT_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.ABERRANT_CRUSHER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.ABERRANT_DRONE_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.ABERRANT_FACEHUGGER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.ABERRANT_OVOMORPH_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.ABERRANT_PRAETORIAN_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.ABERRANT_PROWLER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.ABERRANT_RUNNER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.ABERRANT_WARRIOR_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.ABERRANT_QUEEN_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.ABERRANT_BOILER_SPAWN_EGG);

        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.IRRADIATED_CRUSHER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.IRRADIATED_DRONE_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.IRRADIATED_WARRIOR_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.IRRADIATED_PRAETORIAN_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.IRRADIATED_PROWLER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.IRRADIATED_RUNNER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.IRRADIATED_QUEEN_SPAWN_EGG);

        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.ROYAL_OVOMORPH_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.ROYAL_FACEHUGGER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.ROYAL_CHESTBURSTER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.ROYAL_ADOLESCENT_SPAWN_EGG);

        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.ROYAL_NETHER_OVOMORPH_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.ROYAL_NETHER_FACEHUGGER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.ROYAL_NETHER_CHESTBURSTER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.ROYAL_NETHER_ADOLESCENT_SPAWN_EGG);

        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.ROYAL_ABERRANT_OVOMORPH_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.ROYAL_ABERRANT_FACEHUGGER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.ROYAL_ABERRANT_CHESTBURSTER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AlienSpawnEggItems.ROYAL_ABERRANT_ADOLESCENT_SPAWN_EGG);

        CreativeModeTabUtil.accept(output, PredatorSpawnEggItems.YAUTJA_SPAWN_EGG);
    };
}
