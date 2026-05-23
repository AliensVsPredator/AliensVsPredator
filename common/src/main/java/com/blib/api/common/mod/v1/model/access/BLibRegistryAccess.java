package com.blib.api.common.mod.v1.model.access;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.blib.api.common.mod.v1.BLibMod;
import com.blib.api.common.registry.v1.BLibHolder;
import com.blib.api.common.registry.v1.BLibRegistry;
import com.blib.api.common.registry.v1.impl.BLibAzureLibIdentityRegistry;
import com.blib.api.common.registry.v1.impl.BLibBrewingRegistry;
import com.blib.api.common.registry.v1.impl.BLibCommandRegistry;
import com.blib.api.common.registry.v1.impl.BLibCompostableRegistry;
import com.blib.api.common.registry.v1.impl.BLibDecoratedPotPatternRegistry;
import com.blib.api.common.registry.v1.impl.BLibEntityAttributeRegistry;
import com.blib.api.common.registry.v1.impl.BLibEntitySpawnRegistry;
import com.blib.api.common.registry.v1.impl.BLibFurnaceFuelRegistry;
import com.blib.api.common.registry.v1.impl.BLibNetworkRegistry;
import com.blib.api.common.registry.v1.impl.BLibReloadListenerRegistry;
import com.blib.api.common.registry.v1.impl.BLibVillagerTradeRegistry;
import com.blib.internal.common.registry.impl.BLibItemRegistry;

public class BLibRegistryAccess {

    private final BLibMod mod;

    private final Map<Registry<?>, List<BLibRegistry<?>>> registryToRegistriesMap;

    @ApiStatus.Internal
    public BLibRegistryAccess(BLibMod mod) {
        this.mod = mod;
        this.registryToRegistriesMap = new ConcurrentHashMap<>();
    }

    public BLibBrewingRegistry createBrewingRegistry() {
        return new BLibBrewingRegistry(mod);
    }

    public BLibAzureLibIdentityRegistry createAzureLibIdentityRegistry() {
        return new BLibAzureLibIdentityRegistry(mod);
    }

    public BLibCommandRegistry createCommandRegistry() {
        return new BLibCommandRegistry(mod);
    }

    public BLibCompostableRegistry createCompostableRegistry() {
        return new BLibCompostableRegistry(mod);
    }

    public BLibDecoratedPotPatternRegistry createDecoratedPotPatternRegistry() {
        return new BLibDecoratedPotPatternRegistry(mod);
    }

    public BLibEntityAttributeRegistry createEntityAttributeRegistry() {
        return new BLibEntityAttributeRegistry(mod);
    }

    public BLibEntitySpawnRegistry createEntitySpawnRegistry() {
        return new BLibEntitySpawnRegistry(mod);
    }

    public BLibFurnaceFuelRegistry createFurnaceFuelRegistry() {
        return new BLibFurnaceFuelRegistry(mod);
    }

    public BLibNetworkRegistry createNetworkRegistry() {
        return new BLibNetworkRegistry(mod);
    }

    public BLibReloadListenerRegistry createReloadListenerRegistry() {
        return new BLibReloadListenerRegistry(mod);
    }

    public BLibVillagerTradeRegistry createVillagerTradeRegistry() {
        return new BLibVillagerTradeRegistry(mod);
    }

    public <T> BLibRegistry<T> create(Registry<T> registry) {
        BLibRegistry<T> blibRegistry;

        if (registry == BuiltInRegistries.ITEM) {
            @SuppressWarnings("unchecked")
            var itemRegistry = (BLibRegistry<T>) new BLibItemRegistry(mod);
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
