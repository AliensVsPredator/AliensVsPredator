package com.avp.core.common.util;

import java.util.function.Supplier;

public final class LazySupplier<T> implements Supplier<T> {

    private T value;

    private final Supplier<T> supplier;

    public LazySupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (value == null) {
            value = supplier.get();
        }

        return value;
    }
}
