package com.blib.fabric.internal.client.service.impl;

import org.jetbrains.annotations.ApiStatus;

import com.blib.client.BLibClientMod;
import com.blib.client.event.BLibClientSetupEvent;
import com.blib.common.event.BLibEventListenerHandle;
import com.blib.internal.client.service.BLibClientEventService;

@ApiStatus.Internal
public class BLibFabricClientEventServiceImpl implements BLibClientEventService {

    @Override
    public BLibEventListenerHandle<BLibClientSetupEvent> onClientSetup(BLibClientMod mod) {
        return BLibFabricClientModContainerLookup.INSTANCE.get(mod)
            .onClientSetup();
    }
}
