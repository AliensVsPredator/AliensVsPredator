package com.avp.fabric.service;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;

import java.util.function.Supplier;

import com.avp.AVPResources;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.service.RegistryService;

public class FabricRegistryService implements RegistryService {

    @Override
    public <T> AVPDeferredHolder<T> register(Registry<? super T> registry, String id, Supplier<? extends T> supplier) {
        var registeredElement = Registry.register(registry, AVPResources.location(id), supplier.get());
        var holder = Holder.direct(registeredElement);
        return new AVPDeferredHolder<>(() -> registeredElement, () -> holder);
    }
}
