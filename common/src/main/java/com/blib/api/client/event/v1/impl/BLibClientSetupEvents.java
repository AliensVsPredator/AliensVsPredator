package com.blib.api.client.event.v1.impl;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

import com.blib.api.client.event.v1.BLibClientSetupEvent;
import com.blib.api.client.mod.v1.BLibClientMod;
import com.blib.api.common.event.v1.handle.impl.BLibHookedEventListenerContainer;

public final class BLibClientSetupEvents {

    public static final Function<BLibClientMod, BLibHookedEventListenerContainer<BLibClientMod, BLibClientSetupEvent>> FACTORY =
        BLibHookedEventListenerContainer::new;

    @ApiStatus.Internal
    BLibClientSetupEvents() {
        throw new UnsupportedOperationException();
    }
}
