package com.avp.common.registry.init.creative_mode_tab.initializer;

import com.human.common.registry.init.item.HumanSpawnEggItems;
import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Consumer;

public class SpawnEggsCreativeModeTabInitializer {

    public static final Consumer<CreativeModeTab.Output> OUTPUT_CONSUMER = output -> {
        CreativeModeTabUtil.accept(output, HumanSpawnEggItems.MARINE_SPAWN_EGG);
    };
}
