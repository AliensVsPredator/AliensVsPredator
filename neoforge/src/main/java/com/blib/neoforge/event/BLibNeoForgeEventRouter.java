package com.blib.neoforge.event;

import java.util.ArrayList;
import java.util.List;

import com.blib.common.event.BLibEventRouter;

public abstract class BLibNeoForgeEventRouter<Dispatcher> implements BLibEventRouter<Dispatcher> {

    protected final List<Dispatcher> listeners;

    protected BLibNeoForgeEventRouter() {
        this.listeners = new ArrayList<>();
    }

    public abstract void initialize();

    @Override
    public final void register(Dispatcher dispatcher) {
        listeners.add(dispatcher);
    }
}
