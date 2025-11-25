package com.blib;

import net.minecraft.core.Registry;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.avp.service.Services;

public class BLibRegistry<T> {

    private final Map<String, BLibHolder<? extends T>> pathToHolderMap;

    private final BLibMod mod;

    private final Registry<? super T> registry;

    public BLibRegistry(BLibMod mod, Registry<? super T> registry) {
        this.pathToHolderMap = new ConcurrentHashMap<>();
        this.mod = mod;
        this.registry = registry;
    }

    public void register(BLibHolder<T> holder) {
        var deferredHolder = Services.REGISTRY.register(registry, mod.location(holder.getPath()), holder.getValueSupplier());
        holder.setHolderSupplier(deferredHolder::getHolder);
    }

    public Collection<BLibHolder<? extends T>> getAll() {
        return pathToHolderMap.values();
    }

    public BLibMod getMod() {
        return mod;
    }

    public Registry<? super T> getRegistry() {
        return registry;
    }
}
