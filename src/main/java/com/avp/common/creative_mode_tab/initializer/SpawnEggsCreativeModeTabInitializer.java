package com.avp.common.creative_mode_tab.initializer;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

import com.avp.common.creative_mode_tab.CreativeModeTabs;
import com.avp.common.item.SpawnEggItems;

public class SpawnEggsCreativeModeTabInitializer {

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS_KEY).register(entries -> {
            entries.accept(SpawnEggItems.CHESTBURSTER_SPAWN_EGG);
            entries.accept(SpawnEggItems.DRONE_SPAWN_EGG);
            entries.accept(SpawnEggItems.FACEHUGGER_SPAWN_EGG);
            entries.accept(SpawnEggItems.OVAMORPH_SPAWN_EGG);
            entries.accept(SpawnEggItems.PRAETORIAN_SPAWN_EGG);
            entries.accept(SpawnEggItems.QUEEN_SPAWN_EGG);
            entries.accept(SpawnEggItems.WARRIOR_SPAWN_EGG);

            entries.accept(SpawnEggItems.NETHER_CHESTBURSTER_SPAWN_EGG);
            entries.accept(SpawnEggItems.NETHER_DRONE_SPAWN_EGG);
            entries.accept(SpawnEggItems.NETHER_FACEHUGGER_SPAWN_EGG);
            entries.accept(SpawnEggItems.NETHER_OVAMORPH_SPAWN_EGG);
            entries.accept(SpawnEggItems.NETHER_PRAETORIAN_SPAWN_EGG);
            entries.accept(SpawnEggItems.NETHER_WARRIOR_SPAWN_EGG);

            entries.accept(SpawnEggItems.ABERRANT_CHESTBURSTER_SPAWN_EGG);
            entries.accept(SpawnEggItems.ABERRANT_DRONE_SPAWN_EGG);
            entries.accept(SpawnEggItems.ABERRANT_FACEHUGGER_SPAWN_EGG);
            entries.accept(SpawnEggItems.ABERRANT_OVAMORPH_SPAWN_EGG);
            entries.accept(SpawnEggItems.ABERRANT_PRAETORIAN_SPAWN_EGG);
            entries.accept(SpawnEggItems.ABERRANT_WARRIOR_SPAWN_EGG);

            entries.accept(SpawnEggItems.YAUTJA_SPAWN_EGG);
        });
    }
}
