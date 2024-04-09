package org.avp.common.service;

import net.minecraft.world.item.Item;

import java.util.function.Supplier;

import org.avp.api.GameObject;

public interface ItemRegistry {

    GameObject<Item> register(String registryName, Supplier<Item> supplier);
}
