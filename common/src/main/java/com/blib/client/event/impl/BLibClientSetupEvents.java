package com.blib.client.event.impl;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

import com.blib.client.event.BLibClientSetupEvent;
import com.blib.common.event.impl.BLibEventListenerContainer;

@ApiStatus.Internal
public final class BLibClientSetupEvents {

    public static final Supplier<BLibEventListenerContainer<BLibClientSetupEvent>> CONTAINER_FACTORY = BLibEventListenerContainer::new;

    BLibClientSetupEvents() {
        throw new UnsupportedOperationException();
    }
}
