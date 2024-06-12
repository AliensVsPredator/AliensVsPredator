package org.avp.api.common.registry.holder;

import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BLHolder<T> {

    public static <E> BLHolder<E> empty() {
        return new BLHolder<>(new ResourceLocation("null"), () -> null);
    }

    private T object;

    private final ResourceLocation resourceLocation;

    private final Supplier<T> supplier;

    public BLHolder(ResourceLocation resourceLocation, Supplier<T> supplier) {
        this.resourceLocation = resourceLocation;
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
