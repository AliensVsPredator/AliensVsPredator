package com.blib.api.common.event.v1.handle;

import com.blib.api.common.mod.v1.exception.BLibModInitializationException;
import com.blib.api.common.mod.v1.model.BLibModState;
import com.blib.api.common.mod.v1.model.access.BLibModStateAccess;

public abstract class BLibEventHandle<Dispatcher> implements BLibEventListenerHandle<Dispatcher>, BLibEventDispatchHandle<Dispatcher> {

    private final BLibModStateAccess modStateAccess;

    protected BLibEventHandle(BLibModStateAccess modStateAccess) {
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
