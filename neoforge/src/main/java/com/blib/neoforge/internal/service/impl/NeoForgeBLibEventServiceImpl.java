package com.blib.neoforge.internal.service.impl;

import org.jetbrains.annotations.ApiStatus;

import com.blib.common.event.BLibBlockBreakEvent;
import com.blib.common.event.BLibEventRouter;
import com.blib.common.event.BLibLevelTickEvent;
import com.blib.common.event.BLibTagsUpdatedEvent;
import com.blib.internal.service.BLibEventService;
import com.blib.neoforge.internal.event.impl.NeoForgeBLibLevelTickEvents;
import com.blib.neoforge.internal.event.impl.NeoForgeBLibPlayerBlockBreakEvents;
import com.blib.neoforge.internal.event.impl.NeoForgeBLibTagsUpdatedEvents;

@ApiStatus.Internal
public class NeoForgeBLibEventServiceImpl implements BLibEventService {

    @Override
    public BLibEventRouter<BLibLevelTickEvent> afterLevelTick() {
        return NeoForgeBLibLevelTickEvents.AFTER;
    }

    @Override
    public BLibEventRouter<BLibBlockBreakEvent> beforeBlockBreak() {
        return NeoForgeBLibPlayerBlockBreakEvents.BEFORE;
    }

    @Override
    public BLibEventRouter<BLibLevelTickEvent> beforeLevelTick() {
        return NeoForgeBLibLevelTickEvents.BEFORE;
    }

    @Override
    public BLibEventRouter<BLibTagsUpdatedEvent> onTagsUpdated() {
        return NeoForgeBLibTagsUpdatedEvents.ROUTER;
    }
}
