package org.avp.common.registry;

import net.minecraft.world.item.Item;
import org.avp.api.Holder;
import org.avp.common.service.Services;

import java.util.function.Supplier;

public class AVPDeferredItemRegistry extends AVPDeferredRegistry<Item> {

    @Override
    protected Holder<Item> createHolder(String registryName, Supplier<Item> supplier) {
        var holder = Services.ITEM_REGISTRY.createHolder(registryName, supplier);
        entries.add(holder);
        return holder;
    }

    @Override
    public final void register() {
        entries.forEach(Services.ITEM_REGISTRY::register);
    }
}
