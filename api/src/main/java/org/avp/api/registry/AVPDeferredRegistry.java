package org.avp.api.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.avp.api.registry.holder.BLHolder;

public abstract class AVPDeferredRegistry<T> {

    protected final Map<String, BLHolder<T>> entries;

    protected AVPDeferredRegistry() {
        entries = new HashMap<>();
    }

    protected abstract BLHolder<T> createHolder(String registryName, Supplier<T> supplier);

    public abstract void register();

    public Collection<BLHolder<T>> getValues() {
        return Collections.unmodifiableCollection(entries.values());
    }
}
