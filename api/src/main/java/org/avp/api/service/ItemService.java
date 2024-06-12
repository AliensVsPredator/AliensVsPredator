package org.avp.api.service;

import net.minecraft.world.item.Item;

import java.util.function.Supplier;

import org.avp.api.common.registry.holder.BLHolder;

public interface ItemService {

    BLHolder<Item> createHolder(String registryName, Supplier<Item> supplier);

    void register(BLHolder<Item> holder);
}
