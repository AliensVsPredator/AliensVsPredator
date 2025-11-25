package com.blib;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class BLibMod {

    private final String modId;

    /* package-private */ BLibMod(String modId) {
        this.modId = modId;
    }

    public <T> BLibRegistry<T> createRegistry(Registry<? super T> registry) {
        return new BLibRegistry<>(this, registry);
    }

    public ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(modId, path);
    }

    public String getModId() {
        return modId;
    }
}
