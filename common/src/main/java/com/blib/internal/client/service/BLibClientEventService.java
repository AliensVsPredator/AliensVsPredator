package com.blib.internal.client.service;

import org.jetbrains.annotations.ApiStatus;

import com.blib.client.BLibClientMod;
import com.blib.client.event.BLibClientSetupEvent;
import com.blib.common.event.BLibEventListenerHandle;

@ApiStatus.Internal
public interface BLibClientEventService {

    BLibEventListenerHandle<BLibClientSetupEvent> onClientSetup(BLibClientMod mod);

}
