package org.avp.common.registry.item;

import net.minecraft.world.item.Item;

import org.avp.api.registry.holder.BLHolder;
import org.avp.api.registry.AVPDeferredItemRegistry;

public class AVPSpawnEggItemRegistry extends AVPDeferredItemRegistry {

    public static final AVPSpawnEggItemRegistry INSTANCE = new AVPSpawnEggItemRegistry();

    private AVPSpawnEggItemRegistry() {}

    public void addHolder(String registryName, BLHolder<Item> holder) {
        entries.put(registryName, holder);
    }
}
