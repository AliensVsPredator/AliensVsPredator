package com.blib.common.event.impl;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

import com.blib.common.event.BLibCommonSetupEvent;

@ApiStatus.Internal
public final class BLibCommonSetupEvents {

    public static final Supplier<BLibEventListenerContainer<BLibCommonSetupEvent>> CONTAINER_FACTORY = BLibEventListenerContainer::new;

    BLibCommonSetupEvents() {
        throw new UnsupportedOperationException();
    }
}
