package com.predator.common.registry.init.item;

import com.predator.common.registry.init.PredatorEntityTypes;
import net.minecraft.world.item.Item;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.item.AVPSpawnEggItems;

public class PredatorSpawnEggItems {

    public static final AVPDeferredHolder<Item> YAUTJA_SPAWN_EGG = AVPSpawnEggItems.register(
        "yautja",
        PredatorEntityTypes.YAUTJA,
        0xB9A86C,
        0x5A4728
    );

    public static void initialize() {}
}
