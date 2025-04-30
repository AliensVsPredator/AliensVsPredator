package com.avp.fabric.service;

import net.minecraft.core.Registry;

import java.util.function.Supplier;

import com.avp.AVPResources;
import com.avp.service.RegistryService;

public class FabricRegistryService implements RegistryService {

    @Override
    public <T> Supplier<T> register(Registry<T> registry, String id, Supplier<T> supplier) {
        var registeredElement = Registry.register(registry, AVPResources.location(id), supplier.get());
        return () -> registeredElement;
    }
}
