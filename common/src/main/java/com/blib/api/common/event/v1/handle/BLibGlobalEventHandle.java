package com.blib.api.common.event.v1.handle;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class BLibGlobalEventHandle<T> {

    private final List<T> listeners = new CopyOnWriteArrayList<>();

    public void register(T listener) {
        listeners.add(listener);
    }

    public List<T> listeners() {
        return listeners;
    }

    public boolean hasListeners() {
        return !listeners.isEmpty();
    }
}
