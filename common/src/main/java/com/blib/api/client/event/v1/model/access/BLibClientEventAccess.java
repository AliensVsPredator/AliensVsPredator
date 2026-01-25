package com.blib.api.client.event.v1.model.access;

import org.jetbrains.annotations.ApiStatus;

import com.blib.api.client.event.v1.BLibClientSetupEvent;
import com.blib.api.client.mod.v1.BLibClientMod;
import com.blib.api.common.event.v1.handle.BLibEventListenerHandle;
import com.blib.internal.client.service.BLibInternalClientServices;

public class BLibClientEventAccess {

    private final BLibClientMod mod;

    @ApiStatus.Internal
    public BLibClientEventAccess(BLibClientMod mod) {
        this.mod = mod;
    }

    public BLibEventListenerHandle<BLibClientSetupEvent> onClientSetup() {
        return BLibInternalClientServices.CLIENT_EVENT.onClientSetup(mod);
    }
}
