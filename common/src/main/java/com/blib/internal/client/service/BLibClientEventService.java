package com.blib.internal.client.service;

import org.jetbrains.annotations.ApiStatus;

import com.blib.api.client.event.v1.BLibClientSetupEvent;
import com.blib.api.client.mod.v1.BLibClientMod;
import com.blib.api.common.event.v1.handle.BLibEventListenerHandle;

@ApiStatus.Internal
public interface BLibClientEventService {

    BLibEventListenerHandle<BLibClientSetupEvent> onClientSetup(BLibClientMod mod);

}
