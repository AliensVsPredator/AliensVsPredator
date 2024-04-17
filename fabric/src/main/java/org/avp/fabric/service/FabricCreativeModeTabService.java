package org.avp.fabric.service;

import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Supplier;

import org.avp.api.Holder;
import org.avp.common.service.CreativeModeTabService;
import org.avp.fabric.common.registry.AVPDeferredCreativeTabRegistry;

public class FabricCreativeModeTabService implements CreativeModeTabService {

    @Override
    public Holder<CreativeModeTab> register(String registryName, Supplier<CreativeModeTab> supplier) {
        var holder = new Holder<>(registryName, supplier);
        AVPDeferredCreativeTabRegistry.enqueue(holder);
        return holder;
    }
}
