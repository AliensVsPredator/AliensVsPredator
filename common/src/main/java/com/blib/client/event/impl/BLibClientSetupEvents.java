package com.blib.client.event.impl;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

import com.blib.client.BLibClientMod;
import com.blib.client.event.BLibClientSetupEvent;
import com.blib.common.event.impl.BLibHookedEventListenerContainer;

@ApiStatus.Internal
public final class BLibClientSetupEvents {

    public static final Function<BLibClientMod, BLibHookedEventListenerContainer<BLibClientMod, BLibClientSetupEvent>> CONTAINER_FACTORY =
        BLibHookedEventListenerContainer::new;

    BLibClientSetupEvents() {
        throw new UnsupportedOperationException();
    }
}
