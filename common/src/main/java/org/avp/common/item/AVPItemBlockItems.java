package org.avp.common.item;

import net.minecraft.world.item.Item;

import org.avp.api.Holder;
import org.avp.common.registry.AVPDeferredItemRegistry;

public class AVPItemBlockItems extends AVPDeferredItemRegistry {

    public static final AVPItemBlockItems INSTANCE = new AVPItemBlockItems();

    private AVPItemBlockItems() {}

    public void addHolder(String registryName, Holder<Item> holder) {
        entries.put(registryName, holder);
    }
}
