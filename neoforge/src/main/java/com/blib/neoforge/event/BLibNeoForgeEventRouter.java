package com.blib.neoforge.event;

import java.util.ArrayList;
import java.util.List;

import com.blib.common.event.BLibEventRouter;
import com.blib.common.model.access.BLibModStateAccess;

public abstract class BLibNeoForgeEventRouter<Dispatcher> extends BLibEventRouter<Dispatcher> {

    protected final List<Dispatcher> listeners;

    protected BLibNeoForgeEventRouter(BLibModStateAccess modStateAccess) {
        super(modStateAccess);
        this.listeners = new ArrayList<>();
    }

    public abstract void initialize();

    @Override
    public final void onRegister(Dispatcher dispatcher) {
        listeners.add(dispatcher);
    }
}
