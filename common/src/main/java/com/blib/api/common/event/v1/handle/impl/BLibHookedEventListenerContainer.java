package com.blib.api.common.event.v1.handle.impl;

import com.blib.api.common.mod.v1.exception.BLibModInitializationException;
import com.blib.api.common.mod.v1.model.BLibModState;
import com.blib.api.common.mod.v1.model.access.BLibModStateAccess;

public class BLibHookedEventListenerContainer<T extends BLibModStateAccess, Dispatcher> extends BLibEventListenerContainer<Dispatcher> {

    private final T modStateAccess;

    public BLibHookedEventListenerContainer(T modStateAccess) {
        this.modStateAccess = modStateAccess;
    }

    @Override
    public void register(Dispatcher dispatcher) {
        if (modStateAccess.state() != BLibModState.INITIALIZING) {
            throw new BLibModInitializationException(
                "Attempted to register an event outside of mod's initialization window. Mod State: %s".formatted(modStateAccess.state())
            );
        }

        super.register(dispatcher);
    }
}
