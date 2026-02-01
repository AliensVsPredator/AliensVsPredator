package com.blib.api.common.util.v1;

import com.just.core.functional.option.Option;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

@FunctionalInterface
public interface KeyedAccess<K, V> {

    @Nullable
    V getOrNull(K key);

    default Option<V> get(K key) {
        return Option.ofNullable(getOrNull(key));
    }

    default V getOrDefault(K key, V defaultValue) {
        var value = getOrNull(key);

        return value == null
            ? defaultValue
            : value;
    }

    default V getOrElse(K key, Supplier<V> defaultValueSupplier) {
        var value = getOrNull(key);

        return value == null
            ? defaultValueSupplier.get()
            : value;
    }

    default V getOrThrow(K key) {
        return Objects.requireNonNull(getOrNull(key));
    }

    default boolean has(K key) {
        return getOrNull(key) != null;
    }
}
