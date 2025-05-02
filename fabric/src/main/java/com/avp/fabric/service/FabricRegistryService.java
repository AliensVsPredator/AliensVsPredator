package com.avp.fabric.service;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;

import java.util.function.Supplier;

import com.avp.AVPResources;
import com.avp.service.RegistryService;

public class FabricRegistryService implements RegistryService {

    @Override
    public <T> Supplier<T> register(Registry<? super T> registry, String id, Supplier<? extends T> supplier) {
        var registeredElement = Registry.register(registry, AVPResources.location(id), supplier.get());
        return () -> registeredElement;
    }

    @Override
    public <T> Holder<T> registerHolder(Registry<? super T> registry, String id, Supplier<? extends T> supplier) {
        return Holder.direct(register(registry, id, supplier).get());
    }
}
