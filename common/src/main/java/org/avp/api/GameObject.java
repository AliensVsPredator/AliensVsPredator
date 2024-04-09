package org.avp.api;

import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.Supplier;

import org.avp.common.AVPResources;

public class GameObject<T> {

    public static <E> GameObject<E> empty() {
        return new GameObject<>("null", () -> null);
    }

    private T object;

    private final ResourceLocation resourceLocation;

    private final Supplier<T> supplier;

    public GameObject(String location, Supplier<T> supplier) {
        this.resourceLocation = AVPResources.location(location);
        this.supplier = supplier;
    }

    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }

    public T get() {
        if (object == null) {
            object = supplier.get();
        }
        return object;
    }

    public Optional<T> getOptional() {
        return Optional.ofNullable(get());
    }
}
