package com.blib.common.registry;

import com.blib.BLibMod;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class BLibResourceAccess {

    private final BLibMod mod;

    public BLibResourceAccess(BLibMod mod) {
        this.mod = mod;
    }

    public <T> ResourceKey<T> createKey(ResourceKey<? extends Registry<T>> registryKey, String path) {
        return ResourceKey.create(registryKey, createLocation(path));
    }

    public ResourceLocation createLocation(String path) {
        return ResourceLocation.fromNamespaceAndPath(mod.id(), path);
    }

    public <T> TagKey<T> createTagKey(ResourceKey<? extends Registry<T>> registryKey, String path) {
        return TagKey.create(registryKey, createLocation(path));
    }

}
