package com.blib;

import com.blib.service.BLibServices;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BLibRegistry<T> {

    private final Map<String, BLibHolder<? extends T>> pathToHolderMap;

    private final BLibMod mod;

    private final Registry<? super T> registry;

    private final List<Consumer<BLibHolder<? extends T>>> listeners;

    /* package-private */ BLibRegistry(BLibMod mod, Registry<? super T> registry) {
        this.pathToHolderMap = Collections.synchronizedMap(new LinkedHashMap<>());
        this.mod = mod;
        this.registry = registry;
        this.listeners = new ArrayList<>();
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
        listeners.forEach(listener -> listener.accept(holder));
        return registeredHolder;
    }

    public void addListener(Consumer<BLibHolder<? extends T>> listener) {
        listeners.add(listener);
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
