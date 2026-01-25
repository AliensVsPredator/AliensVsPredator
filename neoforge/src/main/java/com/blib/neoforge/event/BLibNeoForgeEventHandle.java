package com.blib.neoforge.event;

import java.util.ArrayList;
import java.util.List;

import com.blib.api.common.event.v1.handle.BLibEventHandle;
import com.blib.api.common.mod.v1.model.access.BLibModStateAccess;

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
