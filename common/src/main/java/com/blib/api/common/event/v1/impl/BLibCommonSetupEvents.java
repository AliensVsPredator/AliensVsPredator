package com.blib.api.common.event.v1.impl;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

import com.blib.api.common.event.v1.BLibCommonSetupEvent;
import com.blib.api.common.event.v1.handle.impl.BLibEventListenerContainer;
import com.blib.api.common.event.v1.handle.impl.BLibHookedEventListenerContainer;
import com.blib.api.common.mod.v1.BLibMod;

public final class BLibCommonSetupEvents {

    public static final Function<BLibMod, BLibEventListenerContainer<BLibCommonSetupEvent>> FACTORY =
        BLibHookedEventListenerContainer::new;

    @ApiStatus.Internal
    private BLibCommonSetupEvents() {
        throw new UnsupportedOperationException();
    }
}
