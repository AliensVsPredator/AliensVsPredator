package com.blib.api.common.registry.v1;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import com.blib.api.common.mod.v1.BLibMod;
import com.blib.internal.service.BLibInternalServices;

public class BLibCustomRegistryBuilder<T> {

    public static <T> BLibCustomRegistryBuilder<T> create(BLibMod mod, ResourceKey<Registry<T>> registryResourceKey) {
        return new BLibCustomRegistryBuilder<>(mod, registryResourceKey);
    }

    private final BLibMod mod;

    private final ResourceKey<Registry<T>> registryResourceKey;

    private boolean shouldSync;

    private BLibCustomRegistryBuilder(BLibMod mod, ResourceKey<Registry<T>> registryResourceKey) {
        this.mod = mod;
        this.registryResourceKey = registryResourceKey;
    }

    public BLibCustomRegistryBuilder<T> shouldSync(boolean shouldSync) {
        this.shouldSync = shouldSync;
        return this;
    }

    public Registry<T> build() {
        return BLibInternalServices.FACTORY.createCustomRegistry(mod, registryResourceKey, shouldSync);
    }
}
