package org.avp.common.registry.item;

import net.minecraft.world.item.Item;

import org.avp.api.common.registry.AVPDeferredItemRegistry;
import org.avp.api.common.registry.holder.BLHolder;

public class AVPItemBlockItemRegistry extends AVPDeferredItemRegistry {

    public static final AVPItemBlockItemRegistry INSTANCE = new AVPItemBlockItemRegistry();

    private AVPItemBlockItemRegistry() {}

    public void addHolder(String registryName, BLHolder<Item> holder) {
        entries.put(registryName, holder);
    }
}
