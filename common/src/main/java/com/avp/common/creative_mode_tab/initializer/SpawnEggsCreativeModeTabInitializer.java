package com.avp.common.creative_mode_tab.initializer;

import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Consumer;

import com.avp.common.item.AVPSpawnEggItems;

public class SpawnEggsCreativeModeTabInitializer {

    public static final Consumer<CreativeModeTab.Output> OUTPUT_CONSUMER = output -> {
        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.MARINE_SPAWN_EGG);

        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.CHESTBURSTER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.DRONE_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.FACEHUGGER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.OVOMORPH_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.PRAETORIAN_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.QUEEN_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.WARRIOR_SPAWN_EGG);

        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.NETHER_CHESTBURSTER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.NETHER_DRONE_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.NETHER_FACEHUGGER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.NETHER_OVOMORPH_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.NETHER_PRAETORIAN_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.NETHER_WARRIOR_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.NETHER_QUEEN_SPAWN_EGG);

        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.ABERRANT_CHESTBURSTER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.ABERRANT_DRONE_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.ABERRANT_FACEHUGGER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.ABERRANT_OVOMORPH_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.ABERRANT_PRAETORIAN_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.ABERRANT_WARRIOR_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.ABERRANT_QUEEN_SPAWN_EGG);

        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.IRRADIATED_DRONE_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.IRRADIATED_WARRIOR_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.IRRADIATED_PRAETORIAN_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.IRRADIATED_QUEEN_SPAWN_EGG);

        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.ROYAL_OVOMORPH_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.ROYAL_FACEHUGGER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.ROYAL_CHESTBURSTER_SPAWN_EGG);

        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.ROYAL_NETHER_OVOMORPH_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.ROYAL_NETHER_FACEHUGGER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.ROYAL_NETHER_CHESTBURSTER_SPAWN_EGG);

        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.ROYAL_ABERRANT_OVOMORPH_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.ROYAL_ABERRANT_FACEHUGGER_SPAWN_EGG);
        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.ROYAL_ABERRANT_CHESTBURSTER_SPAWN_EGG);

        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.YAUTJA_SPAWN_EGG);
    };
}
