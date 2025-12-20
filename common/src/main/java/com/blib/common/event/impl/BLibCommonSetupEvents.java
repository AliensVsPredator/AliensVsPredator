package com.blib.common.event.impl;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

import com.blib.BLibMod;
import com.blib.common.event.BLibCommonSetupEvent;

@ApiStatus.Internal
public final class BLibCommonSetupEvents {

    public static final Function<BLibMod, BLibEventListenerContainer<BLibCommonSetupEvent>> FACTORY =
        BLibHookedEventListenerContainer::new;

    BLibCommonSetupEvents() {
        throw new UnsupportedOperationException();
    }
}
