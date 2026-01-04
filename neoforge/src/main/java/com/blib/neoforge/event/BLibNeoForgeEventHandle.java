package com.blib.neoforge.event;

import java.util.ArrayList;
import java.util.List;

import com.blib.common.event.handle.BLibEventHandle;
import com.blib.common.model.access.BLibModStateAccess;

public abstract class BLibNeoForgeEventHandle<Dispatcher> extends BLibEventHandle<Dispatcher> {

    protected final List<Dispatcher> listeners;

    protected BLibNeoForgeEventHandle(BLibModStateAccess modStateAccess) {
        super(modStateAccess);
        this.listeners = new ArrayList<>();
    }

    public abstract void initialize();

    @Override
    public final void onRegister(Dispatcher dispatcher) {
        listeners.add(dispatcher);
    }
}
