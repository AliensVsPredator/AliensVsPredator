package com.blib;

import com.blib.internal.service.BLibInternalServices;
import com.blib.mod.BLibModState;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BLibMod {

    private final String id;

    private final Map<Registry<?>, List<BLibRegistry<?>>> registryToRegistriesMap;

    private volatile BLibModState state;

    /* package-private */ BLibMod(String id) {
        this.id = id;
        this.registryToRegistriesMap = new ConcurrentHashMap<>();
        this.state = BLibModState.UNINITIALIZED;
    }

    public void initialize(Runnable runnable) {
        if (state != BLibModState.UNINITIALIZED) {
            throw new BLibModInitializationException("Attempted to initialize a mod that is either initializing or already initialized. Mod State: %s".formatted(state));
        }

        this.state = BLibModState.INITIALIZING;
        runnable.run();
        BLibInternalServices.MOD.postInitialize(this);
        this.state = BLibModState.INITIALIZED;
    }

    public <T> BLibRegistry<T> createRegistry(Registry<T> registry) {
        var newRegistry = new BLibRegistry<>(this, registry);

        registryToRegistriesMap.compute(registry, ($, registries) -> {
            var nonNullRegistries = registries == null
                ? new ArrayList<BLibRegistry<?>>()
                : registries;

            nonNullRegistries.add(newRegistry);

            return nonNullRegistries;
        });

        return newRegistry;
    }

    public <T> ResourceKey<T> createResourceKey(ResourceKey<? extends Registry<T>> registryKey, String path) {
        return ResourceKey.create(registryKey, createResourceLocation(path));
    }

    public ResourceLocation createResourceLocation(String path) {
        return ResourceLocation.fromNamespaceAndPath(id, path);
    }

    public <T> TagKey<T> createTagKey(ResourceKey<? extends Registry<T>> registryKey, String path) {
        return TagKey.create(registryKey, createResourceLocation(path));
    }

    public <T> Collection<BLibHolder<? extends T>> getAllHolders(Registry<? super T> registry) {
        @SuppressWarnings("unchecked")
        var subRegistries = (List<BLibRegistry<T>>) (List<?>) registryToRegistriesMap.getOrDefault(registry, List.of());

        return subRegistries
            .stream()
            .map(BLibRegistry::getAll)
            .flatMap(Collection::stream)
            .toList();
    }

    public String getId() {
        return id;
    }

    public BLibModState getState() {
        return state;
    }
}
