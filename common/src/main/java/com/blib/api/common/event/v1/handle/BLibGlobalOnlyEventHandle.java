package com.blib.api.common.event.v1.handle;

import com.blib.api.common.mod.v1.exception.BLibModInitializationException;
import com.blib.api.common.mod.v1.model.BLibModState;
import com.blib.api.common.mod.v1.model.access.BLibModStateAccess;

public final class BLibGlobalOnlyEventHandle<T> implements BLibEventListenerHandle<T> {

    private final BLibModStateAccess modStateAccess;

    private final BLibGlobalEventHandle<T> globalHandle;

    public BLibGlobalOnlyEventHandle(BLibModStateAccess modStateAccess, BLibGlobalEventHandle<T> globalHandle) {
        this.modStateAccess = modStateAccess;
        this.globalHandle = globalHandle;
    }

    @Override
    public void register(T listener) {
        if (modStateAccess.state() != BLibModState.INITIALIZING) {
            throw new BLibModInitializationException(
                "Attempted to register an event outside of mod's initialization window. Mod State: %s".formatted(modStateAccess.state())
            );
        }

        globalHandle.register(listener);
    }
}
