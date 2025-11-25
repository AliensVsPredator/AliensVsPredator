package com.blib;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class BLibHolder<T> implements Supplier<T> {

    private final BLibRegistry<? super T> registry;

    private final String path;

    private final Supplier<? extends T> valueFactory;

    private Supplier<? extends Holder<T>> holderSupplier;

    /* package-private */ BLibHolder(BLibRegistry<? super T> registry, String path, Supplier<? extends T> valueFactory) {
        this.registry = registry;
        this.path = path;
        this.valueFactory = valueFactory;
    }

    @Override
    public T get() {
        return getHolder().value();
    }

    public Holder<T> getHolder() {
        if (holderSupplier == null) {
            throw new IllegalStateException("Attempted to access unregistered BLibHolder's holder. Path: %s".formatted(path));
        }

        return holderSupplier.get();
    }

    public String getPath() {
        return path;
    }

    public BLibRegistry<? super T> getRegistry() {
        return registry;
    }

    public Supplier<? extends T> getValueFactory() {
        return valueFactory;
    }

    /* package-private */ void setHolderSupplier(Supplier<? extends Holder<T>> holderSupplier) {
        if (this.holderSupplier != null) {
            throw new IllegalStateException(
                "Cannot overwrite existing holder supplier for BLibHolder. Holder: %s".formatted(this.holderSupplier.get())
            );
        }

        this.holderSupplier = holderSupplier;
    }

    public ResourceLocation getResourceLocation() {
        return getRegistry().getMod().createResourceLocation(path);
    }
}
