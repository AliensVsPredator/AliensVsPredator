package org.avp.api.util;

import java.util.function.Supplier;

public class Lazy<T> {

    public static <T> Lazy<T> create(Supplier<T> supplier) {
        return new Lazy<>(supplier);
    }

    private T value;

    private final Supplier<T> supplier;

    private Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if (value == null) {
            value = supplier.get();
        }

        return value;
    }
}
