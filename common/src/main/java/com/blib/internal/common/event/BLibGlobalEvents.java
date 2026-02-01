package com.blib.internal.common.event;

import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.event.v1.BLibEntityTickEvent;
import com.blib.api.common.event.v1.handle.BLibGlobalEventHandle;

@ApiStatus.Internal
public final class BLibGlobalEvents {

    public static final BLibGlobalEventHandle<BLibEntityTickEvent> ENTITY_TICK =
        new BLibGlobalEventHandle<>();

    private BLibGlobalEvents() {}
}
