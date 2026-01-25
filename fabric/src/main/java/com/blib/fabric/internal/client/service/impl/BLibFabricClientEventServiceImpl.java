package com.blib.fabric.internal.client.service.impl;

import org.jetbrains.annotations.ApiStatus;

import com.blib.api.client.event.v1.BLibClientSetupEvent;
import com.blib.api.client.mod.v1.BLibClientMod;
import com.blib.api.common.event.v1.handle.BLibEventListenerHandle;
import com.blib.internal.client.service.BLibClientEventService;

@ApiStatus.Internal
public class BLibFabricClientEventServiceImpl implements BLibClientEventService {

    @Override
    public BLibEventListenerHandle<BLibClientSetupEvent> onClientSetup(BLibClientMod mod) {
        return BLibFabricClientModContainerLookup.INSTANCE.get(mod)
            .onClientSetup();
    }
}
