package org.avp.common.registry;

import org.avp.api.GameObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class AVPDeferredRegistry<T> {

    protected final List<GameObject<T>> entries;

    protected AVPDeferredRegistry() {
        entries = new ArrayList<>();
    }

    protected abstract GameObject<T> createHolder(String registryName, Supplier<T> supplier);

    public abstract void register();

    public List<GameObject<T>> getEntries() {
        return entries;
    }
}
