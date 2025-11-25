package com.blib;

import net.minecraft.core.Holder;

import java.util.function.Supplier;

public class BLibHolder<T> implements Supplier<T> {

    public static <T> BLibHolder<T> create(String path, Supplier<? extends T> valueSupplier) {
        return new BLibHolder<>(path, valueSupplier);
    }

    private final String path;

    private final Supplier<? extends T> valueSupplier;

    private Supplier<? extends Holder<? extends T>> holderSupplier;

    private BLibHolder(String path, Supplier<? extends T> valueSupplier) {
        this.path = path;
        this.valueSupplier = valueSupplier;
    }

    @Override
    public T get() {
        if (holderSupplier == null) {
            throw new IllegalStateException("Attempted to access unregistered BLibHolder's value. Path: %s".formatted(path));
        }

        return valueSupplier.get();
    }

    public Holder<? extends T> getHolder() {
        if (holderSupplier == null) {
            throw new IllegalStateException("Attempted to access unregistered BLibHolder's holder. Path: %s".formatted(path));
        }

        return holderSupplier.get();
    }

    public String getPath() {
        return path;
    }

    /* package-private */ Supplier<? extends T> getValueSupplier() {
        return valueSupplier;
    }

    /* package-private */ void setHolderSupplier(Supplier<? extends Holder<? extends T>> holderSupplier) {
        if (this.holderSupplier != null) {
            throw new IllegalStateException(
                "Cannot overwrite existing holder supplier for BLibHolder. Holder: %s".formatted(this.holderSupplier.get())
            );
        }

        this.holderSupplier = holderSupplier;
    }
}
