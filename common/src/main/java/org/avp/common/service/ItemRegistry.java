package org.avp.common.service;

import net.minecraft.world.item.Item;

import java.util.function.Supplier;

import org.avp.api.GameObject;

/**
 * @author Boston Vanseghi
 */
public interface ItemRegistry {

    GameObject<Item> register(String registryName, Supplier<Item> supplier);
}
