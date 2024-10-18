package org.avp.fabric.service;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

import org.avp.api.common.registry.holder.BLHolder;
import org.avp.api.service.ItemService;
import org.avp.common.registry.holder.AVPHolder;

public class FabricItemService implements ItemService {

    @Override
    public BLHolder<Item> createHolder(String registryName, Supplier<Item> supplier) {
        return new AVPHolder<>(registryName, supplier);
    }

    @Override
    public void register(BLHolder<Item> holder) {
        Registry.register(BuiltInRegistries.ITEM, holder.getResourceLocation(), holder.get());
    }
}
