package com.blib.neoforge.internal.client.service.impl;

import org.jetbrains.annotations.ApiStatus;

import com.blib.client.BLibClientMod;
import com.blib.client.event.BLibClientSetupEvent;
import com.blib.common.event.BLibEventListenerHandle;
import com.blib.internal.client.service.BLibClientEventService;

@ApiStatus.Internal
public class BLibNeoForgeClientEventServiceImpl implements BLibClientEventService {

    @Override
    public BLibEventListenerHandle<BLibClientSetupEvent> onClientSetup(BLibClientMod mod) {
        return BLibNeoForgeClientModContainerLookup.INSTANCE.get(mod)
            .onClientSetup();
    }
}
