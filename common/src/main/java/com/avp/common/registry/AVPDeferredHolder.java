package com.avp.common.registry;

import net.minecraft.core.Holder;

import java.util.function.Supplier;

public class AVPDeferredHolder<T> implements Supplier<T> {

    private final Supplier<T> valueSupplier;

    private final Supplier<Holder<T>> valueHolderSupplier;

    public AVPDeferredHolder(Supplier<T> valueSupplier, Supplier<Holder<T>> valueHolderSupplier) {
        this.valueSupplier = valueSupplier;
        this.valueHolderSupplier = valueHolderSupplier;
    }

    @Override
    public T get() {
        return valueSupplier.get();
    }

    public Holder<T> getHolder() {
        return valueHolderSupplier.get();
    }
}
