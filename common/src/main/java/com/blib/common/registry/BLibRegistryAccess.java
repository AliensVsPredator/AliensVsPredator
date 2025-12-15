package com.blib.common.registry;

import com.blib.BLibMod;
import com.blib.common.registry.impl.BLibCompostableRegistry;
import com.blib.common.registry.impl.BLibDecoratedPotPatternRegistry;
import com.blib.common.registry.impl.BLibEntityTypeRegistry;
import com.blib.common.registry.impl.BLibFurnaceFuelRegistry;
import com.blib.common.registry.impl.BLibItemRegistry;
import com.blib.common.registry.impl.BLibReloadListenerRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BLibRegistryAccess {

    private final BLibMod mod;

    private final Map<Registry<?>, List<BLibRegistry<?>>> registryToRegistriesMap;

    public BLibRegistryAccess(BLibMod mod) {
        this.mod = mod;
        this.registryToRegistriesMap = new ConcurrentHashMap<>();
    }

    public BLibCompostableRegistry createCompostableRegistry() {
        return new BLibCompostableRegistry(mod);
    }

    public BLibDecoratedPotPatternRegistry createDecoratedPotPatternRegistry() {
        return new BLibDecoratedPotPatternRegistry(mod);
    }

    public BLibEntityTypeRegistry createEntityTypeRegistry() {
        return bind(new BLibEntityTypeRegistry(mod));
    }

    public BLibFurnaceFuelRegistry createFurnaceFuelRegistry() {
        return new BLibFurnaceFuelRegistry(mod);
    }

    public BLibItemRegistry createItemRegistry() {
        return bind(new BLibItemRegistry(mod));
    }

    public BLibReloadListenerRegistry createReloadListenerRegistry() {
        return new BLibReloadListenerRegistry(mod);
    }

    public <T> BLibRegistry<T> create(Registry<T> registry) {
        BLibRegistry<T> blibRegistry;

        if (registry == BuiltInRegistries.ENTITY_TYPE) {
            @SuppressWarnings("unchecked")
            var entityTypeRegistry = (BLibRegistry<T>) createEntityTypeRegistry();
            blibRegistry = entityTypeRegistry;
        } else if (registry == BuiltInRegistries.ITEM) {
            @SuppressWarnings("unchecked")
            var itemRegistry = (BLibRegistry<T>) createItemRegistry();
            blibRegistry = itemRegistry;
        } else {
            blibRegistry = new BLibRegistry<>(mod, registry);
        }

        return bind(blibRegistry);
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

    private <T, U extends BLibRegistry<T>> U bind(U blibRegistry) {
        registryToRegistriesMap.compute(blibRegistry.getBackingRegistry(), ($, registries) -> {
            var nonNullRegistries = registries == null
                ? new ArrayList<BLibRegistry<?>>()
                : registries;

            nonNullRegistries.add(blibRegistry);

            return nonNullRegistries;
        });

        return blibRegistry;
    }
}
