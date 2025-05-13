package com.avp.common.util;

import com.bvanseg.just.functional.option.Option;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class Cache<K, T> {

    private final Map<K, T> cache;

    protected Cache() {
        this.cache = new HashMap<>();
    }

    public final void addToCache(K id, T value) {
        var oldValue = cache.put(id, value);

        onAddToCache(id, oldValue, value);
    }

    public final Option<T> getFromCache(K id) {
        return Option.ofNullable(cache.get(id));
    }

    public final boolean isInCache(K id) {
        return cache.containsKey(id);
    }

    public final void removeFromCache(K id) {
        var value = cache.remove(id);

        if (value != null) {
            onRemoveFromCache(id, value);
        }
    }

    public final void clearCache() {
        cache.clear();
        onClearCache();
    }

    public void removeFromCacheIf(Predicate<Map.Entry<K, T>> predicate) {
        var entriesToRemove = cache.entrySet()
            .stream()
            .filter(predicate)
            .toList();

        entriesToRemove.forEach(entry -> removeFromCache(entry.getKey()));
    }

    public final Stream<Map.Entry<K, T>> streamCacheEntries() {
        return List.copyOf(cache.entrySet()).stream();
    }

    protected void onAddToCache(K id, @Nullable T oldValue, T newValue) {}

    protected void onRemoveFromCache(K id, T value) {}

    protected void onClearCache() {}
}
