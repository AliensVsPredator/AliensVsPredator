package org.avp.common.service;

import net.minecraft.world.item.Item;

import java.util.function.Supplier;

import org.avp.api.Holder;

public interface ItemService {

    Holder<Item> createHolder(String registryName, Supplier<Item> supplier);

    void register(Holder<Item> holder);
}
