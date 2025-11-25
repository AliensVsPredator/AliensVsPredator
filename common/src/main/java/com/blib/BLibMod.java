package com.blib;

import com.blib.event.key.BLibEventKey;
import com.blib.service.BLibServices;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Consumer;

public class BLibMod {

    private final String id;

    /* package-private */ BLibMod(String id) {
        this.id = id;
    }

    public <T> void addEventListener(BLibEventKey<T> key, Consumer<T> consumer) {
        BLibServices.EVENT.addListener(key, consumer);
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
