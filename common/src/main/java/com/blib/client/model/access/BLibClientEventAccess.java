package com.blib.client.model.access;

import org.jetbrains.annotations.ApiStatus;

import com.blib.client.BLibClientMod;
import com.blib.client.event.BLibClientSetupEvent;
import com.blib.common.event.BLibEventListenerHandle;
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
