package com.blib.common.event;

import com.blib.common.exception.BLibModInitializationException;
import com.blib.common.model.BLibModState;
import com.blib.common.model.access.BLibModStateAccess;

public abstract class BLibEventRouter<Dispatcher> implements BLibEventListenerHandle<Dispatcher>, BLibEventDispatchHandle<Dispatcher> {

    private final BLibModStateAccess modStateAccess;

    protected BLibEventRouter(BLibModStateAccess modStateAccess) {
        this.modStateAccess = modStateAccess;
    }

    @Override
    public final void register(Dispatcher dispatcher) {
        if (modStateAccess.state() != BLibModState.INITIALIZING) {
            throw new BLibModInitializationException(
                "Attempted to register an event outside of mod's initialization window. Mod State: %s".formatted(modStateAccess.state())
            );
        }

        onRegister(dispatcher);
    }

    public abstract void onRegister(Dispatcher dispatcher);
}
