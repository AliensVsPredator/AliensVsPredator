package org.avp.common.registry;

import org.avp.api.GameObject;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public abstract class AVPDeferredRegistry<T> {

    protected final Set<GameObject<T>> entries;

    protected AVPDeferredRegistry() {
        entries = ConcurrentHashMap.newKeySet();
    }

    protected abstract GameObject<T> createHolder(String registryName, Supplier<T> supplier);

    public abstract void register();

    public Set<GameObject<T>> getEntries() {
        return entries;
    }
}
