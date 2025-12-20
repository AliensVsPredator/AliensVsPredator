package com.blib.common.event.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.blib.common.event.BLibEventListenerHandle;

public class BLibEventListenerContainer<Dispatcher> implements BLibEventListenerHandle<Dispatcher> {

    private final List<Dispatcher> listeners;

    public BLibEventListenerContainer() {
        this.listeners = new ArrayList<>();
    }

    @Override
    public void register(Dispatcher dispatcher) {
        listeners.add(dispatcher);
    }

    public List<Dispatcher> getListeners() {
        return Collections.unmodifiableList(listeners);
    }
}
