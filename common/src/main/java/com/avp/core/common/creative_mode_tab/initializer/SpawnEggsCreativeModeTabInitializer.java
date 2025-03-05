package com.avp.core.common.creative_mode_tab.initializer;

import com.avp.core.common.creative_mode_tab.AVPCreativeModeTabOutputAdapter;
import com.avp.core.common.item.SpawnEggItems;
import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Consumer;

public class SpawnEggsCreativeModeTabInitializer {

    public static final Consumer<CreativeModeTab.Output> CONSUMER = output -> {
        var adaptedOutput = new AVPCreativeModeTabOutputAdapter(output);

        adaptedOutput.accept(SpawnEggItems.CHESTBURSTER_SPAWN_EGG);
        adaptedOutput.accept(SpawnEggItems.DRONE_SPAWN_EGG);
        adaptedOutput.accept(SpawnEggItems.FACEHUGGER_SPAWN_EGG);
        adaptedOutput.accept(SpawnEggItems.OVAMORPH_SPAWN_EGG);
        adaptedOutput.accept(SpawnEggItems.PRAETORIAN_SPAWN_EGG);
        adaptedOutput.accept(SpawnEggItems.QUEEN_SPAWN_EGG);
        adaptedOutput.accept(SpawnEggItems.WARRIOR_SPAWN_EGG);

        adaptedOutput.accept(SpawnEggItems.NETHER_CHESTBURSTER_SPAWN_EGG);
        adaptedOutput.accept(SpawnEggItems.NETHER_DRONE_SPAWN_EGG);
        adaptedOutput.accept(SpawnEggItems.NETHER_FACEHUGGER_SPAWN_EGG);
        adaptedOutput.accept(SpawnEggItems.NETHER_OVAMORPH_SPAWN_EGG);
        adaptedOutput.accept(SpawnEggItems.NETHER_PRAETORIAN_SPAWN_EGG);
        adaptedOutput.accept(SpawnEggItems.NETHER_WARRIOR_SPAWN_EGG);

        adaptedOutput.accept(SpawnEggItems.ABERRANT_CHESTBURSTER_SPAWN_EGG);
        adaptedOutput.accept(SpawnEggItems.ABERRANT_DRONE_SPAWN_EGG);
        adaptedOutput.accept(SpawnEggItems.ABERRANT_FACEHUGGER_SPAWN_EGG);
        adaptedOutput.accept(SpawnEggItems.ABERRANT_OVAMORPH_SPAWN_EGG);
        adaptedOutput.accept(SpawnEggItems.ABERRANT_PRAETORIAN_SPAWN_EGG);
        adaptedOutput.accept(SpawnEggItems.ABERRANT_WARRIOR_SPAWN_EGG);

        adaptedOutput.accept(SpawnEggItems.YAUTJA_SPAWN_EGG);
    };
}
