package com.blib.api.common.registry.v1;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.blib.api.common.mod.v1.BLibMod;
import com.blib.api.common.mod.v1.model.BLibModState;
import com.blib.api.common.registry.v1.exception.BLibRegistrationException;
import com.blib.internal.service.BLibInternalServices;

public class BLibRegistry<T> {

    private final Map<String, BLibHolder<? extends T>> pathToHolderMap;

    private final Map<String, Supplier<? extends T>> pathToValueFactoryMap;

    private final BLibMod mod;

    private final Registry<? super T> registry;

    private final List<Consumer<BLibHolder<? extends T>>> listeners;

    @ApiStatus.Internal
    public BLibRegistry(BLibMod mod, Registry<? super T> registry) {
        this.pathToHolderMap = Collections.synchronizedMap(new LinkedHashMap<>());
        this.pathToValueFactoryMap = new HashMap<>();
        this.mod = mod;
        this.registry = registry;
        this.listeners = new ArrayList<>();
    }

    public <U extends T> BLibHolder<U> createHolder(String path, Supplier<U> valueSupplier) {
        var holder = new BLibHolder<U>(this, path);
        pathToHolderMap.put(path, holder);
        pathToValueFactoryMap.put(path, valueSupplier);
        return holder;
    }

    public void registerAll() {
        getAll().forEach(this::register);
    }

    public <U extends T> Holder<U> register(BLibHolder<U> holder) {
        if (mod.state() != BLibModState.INITIALIZING) {
            throw new BLibRegistrationException(
                "Attempted to register a BLibHolder outside of mod's initialization window. BLibHolder: %s, Mod State: %s".formatted(
                    holder,
                    mod.state()
                )
            );
        }

        var path = holder.getPath();
        @SuppressWarnings("unchecked")
        var valueFactory = (Supplier<U>) Objects.requireNonNull(
            pathToValueFactoryMap.get(path),
            "Attempted to register BLibHolder with no backing value factory. Path: %s".formatted(path)
        );
        var registeredHolder = BLibInternalServices.REGISTRY.register(holder, valueFactory);

        listeners.forEach(listener -> listener.accept(holder));

        return registeredHolder;
    }

    public void addListener(Consumer<BLibHolder<? extends T>> listener) {
        listeners.add(listener);
    }

    public List<? extends T> computeMissingEntries(Collection<T> entries) {
        return mod.registries()
            .<T>getAllHolders(getBackingRegistry())
            .stream()
            .map(Supplier::get)
            .filter(Predicate.not(entries::contains))
            .toList();
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
