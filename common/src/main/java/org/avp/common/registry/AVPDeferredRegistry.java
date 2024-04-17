package org.avp.common.registry;

import org.avp.api.Holder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class AVPDeferredRegistry<T> {

    protected final List<Holder<T>> entries;

    protected AVPDeferredRegistry() {
        entries = new ArrayList<>();
    }

    protected abstract Holder<T> createHolder(String registryName, Supplier<T> supplier);

    public abstract void register();

    public List<Holder<T>> getEntries() {
        return entries;
    }
}
