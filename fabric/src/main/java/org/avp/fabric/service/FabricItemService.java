package org.avp.fabric.service;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

import org.avp.api.Holder;
import org.avp.common.service.ItemService;

public class FabricItemService implements ItemService {

    @Override
    public Holder<Item> createHolder(String registryName, Supplier<Item> supplier) {
        return new Holder<>(registryName, supplier);
    }

    @Override
    public void register(Holder<Item> holder) {
        Registry.register(BuiltInRegistries.ITEM, holder.getResourceLocation(), holder.get());
    }
}
