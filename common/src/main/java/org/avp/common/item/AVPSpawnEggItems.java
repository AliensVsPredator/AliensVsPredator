package org.avp.common.item;

import net.minecraft.world.item.Item;

import org.avp.api.Holder;
import org.avp.common.registry.AVPDeferredItemRegistry;

public class AVPSpawnEggItems extends AVPDeferredItemRegistry {

    public static final AVPSpawnEggItems INSTANCE = new AVPSpawnEggItems();

    private AVPSpawnEggItems() {}

    public void addHolder(Holder<Item> holder) {
        entries.add(holder);
    }
}
