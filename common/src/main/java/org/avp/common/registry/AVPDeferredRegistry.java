package org.avp.common.registry;

import org.avp.api.Holder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class AVPDeferredRegistry<T> {

    protected final Map<String, Holder<T>> entries;

    protected AVPDeferredRegistry() {
        entries = new HashMap<>();
    }

    protected abstract Holder<T> createHolder(String registryName, Supplier<T> supplier);

    public abstract void register();

    public Collection<Holder<T>> getValues() {
        return Collections.unmodifiableCollection(entries.values());
    }
}
