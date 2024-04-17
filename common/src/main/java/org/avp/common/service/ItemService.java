package org.avp.common.service;

import net.minecraft.world.item.Item;

import java.util.function.Supplier;

import org.avp.api.GameObject;

public interface ItemService {

    GameObject<Item> createHolder(String registryName, Supplier<Item> supplier);

    void register(GameObject<Item> holder);
}
