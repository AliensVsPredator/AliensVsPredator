package com.human.common.registry.init.item;

import com.human.common.registry.init.entity_type.HumanEntityTypes;
import net.minecraft.world.item.Item;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.item.AVPSpawnEggItems;

public class HumanSpawnEggItems {

    public static final AVPDeferredHolder<Item> MARINE_SPAWN_EGG = AVPSpawnEggItems.register(
        "marine",
        HumanEntityTypes.MARINE,
        0x5a5941,
        0x414441
    );

    public static void initialize() {}
}
