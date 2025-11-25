package com.blib;

import com.blib.service.BLibServices;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class BLibRegistry<T> {

    private final Map<String, BLibHolder<? extends T>> pathToHolderMap;

    private final BLibMod mod;

    private final Registry<? super T> registry;

    public BLibRegistry(BLibMod mod, Registry<? super T> registry) {
        this.pathToHolderMap = new ConcurrentHashMap<>();
        this.mod = mod;
        this.registry = registry;
    }

    public <U extends T> BLibHolder<U> createHolder(String path, Supplier<U> valueSupplier) {
        var holder = new BLibHolder<>(this, path, valueSupplier);
        pathToHolderMap.put(path, holder);
        return holder;
    }

    public void registerAll() {
        getAll().forEach(this::register);
    }

    public <U extends T> Holder<U> register(BLibHolder<U> holder) {
        var registeredHolder = BLibServices.REGISTRY.register(holder);
        holder.setHolderSupplier(() -> registeredHolder);
        return registeredHolder;
    }

    public Collection<BLibHolder<? extends T>> getAll() {
        return Collections.unmodifiableCollection(pathToHolderMap.values());
    }

    public BLibMod getMod() {
        return mod;
    }

    public Registry<? super T> getBackingRegistry() {
        return registry;
    }
}
