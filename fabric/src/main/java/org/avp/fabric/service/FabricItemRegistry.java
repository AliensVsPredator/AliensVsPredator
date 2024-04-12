package org.avp.fabric.service;

import net.minecraft.world.item.Item;

import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.common.service.ItemRegistry;
import org.avp.fabric.common.registry.AVPDeferredItemRegistry;

public class FabricItemRegistry implements ItemRegistry {

    @Override
    public GameObject<Item> register(String registryName, Supplier<Item> supplier) {
        var gameObject = new GameObject<>(registryName, supplier);
        AVPDeferredItemRegistry.enqueue(gameObject);
        return gameObject;
    }
}
