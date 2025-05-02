package com.avp.common.creative_mode_tab.initializer;

import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Consumer;

import com.avp.common.item.AVPSpawnEggItems;

public class SpawnEggsCreativeModeTabInitializer {

    // FIXME:
    public static final Consumer<CreativeModeTab.Output> OUTPUT_CONSUMER = output -> {
        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.MARINE_SPAWN_EGG);

        // CreativeModeTabUtil.accept(output, SpawnEggItems.CHESTBURSTER_SPAWN_EGG);
        // CreativeModeTabUtil.accept(output, SpawnEggItems.DRONE_SPAWN_EGG);
        // CreativeModeTabUtil.accept(output, SpawnEggItems.FACEHUGGER_SPAWN_EGG);
        // CreativeModeTabUtil.accept(output, SpawnEggItems.OVAMORPH_SPAWN_EGG);
        // CreativeModeTabUtil.accept(output, SpawnEggItems.PRAETORIAN_SPAWN_EGG);
        // CreativeModeTabUtil.accept(output, SpawnEggItems.QUEEN_SPAWN_EGG);
        // CreativeModeTabUtil.accept(output, SpawnEggItems.WARRIOR_SPAWN_EGG);
        //
        // CreativeModeTabUtil.accept(output, SpawnEggItems.NETHER_CHESTBURSTER_SPAWN_EGG);
        // CreativeModeTabUtil.accept(output, SpawnEggItems.NETHER_DRONE_SPAWN_EGG);
        // CreativeModeTabUtil.accept(output, SpawnEggItems.NETHER_FACEHUGGER_SPAWN_EGG);
        // CreativeModeTabUtil.accept(output, SpawnEggItems.NETHER_OVAMORPH_SPAWN_EGG);
        // CreativeModeTabUtil.accept(output, SpawnEggItems.NETHER_PRAETORIAN_SPAWN_EGG);
        // CreativeModeTabUtil.accept(output, SpawnEggItems.NETHER_WARRIOR_SPAWN_EGG);
        // CreativeModeTabUtil.accept(output, SpawnEggItems.NETHER_QUEEN_SPAWN_EGG);
        //
        // CreativeModeTabUtil.accept(output, SpawnEggItems.ABERRANT_CHESTBURSTER_SPAWN_EGG);
        // CreativeModeTabUtil.accept(output, SpawnEggItems.ABERRANT_DRONE_SPAWN_EGG);
        // CreativeModeTabUtil.accept(output, SpawnEggItems.ABERRANT_FACEHUGGER_SPAWN_EGG);
        // CreativeModeTabUtil.accept(output, SpawnEggItems.ABERRANT_OVAMORPH_SPAWN_EGG);
        // CreativeModeTabUtil.accept(output, SpawnEggItems.ABERRANT_PRAETORIAN_SPAWN_EGG);
        // CreativeModeTabUtil.accept(output, SpawnEggItems.ABERRANT_WARRIOR_SPAWN_EGG);
        // CreativeModeTabUtil.accept(output, SpawnEggItems.ABERRANT_QUEEN_SPAWN_EGG);
        //
        // CreativeModeTabUtil.accept(output, SpawnEggItems.IRRADIATED_DRONE_SPAWN_EGG);
        // CreativeModeTabUtil.accept(output, SpawnEggItems.IRRADIATED_WARRIOR_SPAWN_EGG);
        // CreativeModeTabUtil.accept(output, SpawnEggItems.IRRADIATED_PRAETORIAN_SPAWN_EGG);
        // CreativeModeTabUtil.accept(output, SpawnEggItems.IRRADIATED_QUEEN_SPAWN_EGG);
        //
        // CreativeModeTabUtil.accept(output, SpawnEggItems.ROYAL_OVAMORPH_SPAWN_EGG);
        // CreativeModeTabUtil.accept(output, SpawnEggItems.ROYAL_FACEHUGGER_SPAWN_EGG);
        // CreativeModeTabUtil.accept(output, SpawnEggItems.ROYAL_CHESTBURSTER_SPAWN_EGG);
        //
        // CreativeModeTabUtil.accept(output, SpawnEggItems.ROYAL_NETHER_OVAMORPH_SPAWN_EGG);
        // CreativeModeTabUtil.accept(output, SpawnEggItems.ROYAL_NETHER_FACEHUGGER_SPAWN_EGG);
        // CreativeModeTabUtil.accept(output, SpawnEggItems.ROYAL_NETHER_CHESTBURSTER_SPAWN_EGG);
        //
        // CreativeModeTabUtil.accept(output, SpawnEggItems.ROYAL_ABERRANT_OVAMORPH_SPAWN_EGG);
        // CreativeModeTabUtil.accept(output, SpawnEggItems.ROYAL_ABERRANT_FACEHUGGER_SPAWN_EGG);
        // CreativeModeTabUtil.accept(output, SpawnEggItems.ROYAL_ABERRANT_CHESTBURSTER_SPAWN_EGG);

        CreativeModeTabUtil.accept(output, AVPSpawnEggItems.YAUTJA_SPAWN_EGG);
    };
}
