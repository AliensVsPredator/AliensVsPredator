package com.blib;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class BLibMod {

    private final String id;

    /* package-private */ BLibMod(String id) {
        this.id = id;
    }

    public <T> BLibRegistry<T> createRegistry(Registry<? super T> registry) {
        return new BLibRegistry<>(this, registry);
    }

    public ResourceLocation createResourceLocation(String path) {
        return ResourceLocation.fromNamespaceAndPath(id, path);
    }

    public String getId() {
        return id;
    }
}
