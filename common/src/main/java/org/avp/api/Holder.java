package org.avp.api;

import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.avp.common.AVPResources;

public class Holder<T> {

    public static <E> Holder<E> empty() {
        return new Holder<>("null", () -> null);
    }

    private T object;

    private final ResourceLocation resourceLocation;

    private final Supplier<T> supplier;

    public Holder(String location, Supplier<T> supplier) {
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

    public void ifPresent(Consumer<T> consumer) {
        if (object != null) {
            consumer.accept(object);
        }
    }
}
