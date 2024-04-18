package org.avp.common.registry;

import net.minecraft.world.item.Item;

import java.util.function.Supplier;

import org.avp.api.Holder;
import org.avp.common.service.Services;

public class AVPDeferredItemRegistry extends AVPDeferredRegistry<Item> {

    @Override
    protected Holder<Item> createHolder(String registryName, Supplier<Item> supplier) {
        var holder = Services.ITEM_SERVICE.createHolder(registryName, supplier);
        entries.add(holder);
        return holder;
    }

    protected Holder<Item> createHolder(String registryName) {
        return createHolder(registryName, () -> new Item(new Item.Properties()));
    }

    @Override
    public final void register() {
        entries.forEach(Services.ITEM_SERVICE::register);
    }
}
