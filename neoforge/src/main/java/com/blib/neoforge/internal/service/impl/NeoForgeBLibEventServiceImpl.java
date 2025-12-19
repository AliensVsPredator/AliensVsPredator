package com.blib.neoforge.internal.service.impl;

import org.jetbrains.annotations.ApiStatus;

import com.blib.common.event.BLibBlockBreakEvent;
import com.blib.common.event.BLibEventRouter;
import com.blib.common.event.BLibLevelTickEvent;
import com.blib.common.event.BLibPlayerTrackingEntityEvent;
import com.blib.common.event.BLibTagsUpdatedEvent;
import com.blib.internal.service.BLibEventService;
import com.blib.neoforge.internal.event.impl.BLibNeoForgePlayerTrackingEntityEvents;
import com.blib.neoforge.internal.event.impl.NeoForgeBLibLevelTickEvents;
import com.blib.neoforge.internal.event.impl.NeoForgeBLibPlayerBlockBreakEvents;
import com.blib.neoforge.internal.event.impl.NeoForgeBLibTagsUpdatedEvents;

@ApiStatus.Internal
public class NeoForgeBLibEventServiceImpl implements BLibEventService {

    @Override
    public BLibEventRouter<BLibPlayerTrackingEntityEvent> onPlayerStartTrackingEntity() {
        return BLibNeoForgePlayerTrackingEntityEvents.START;
    }

    @Override
    public BLibEventRouter<BLibTagsUpdatedEvent> onTagsUpdated() {
        return NeoForgeBLibTagsUpdatedEvents.ROUTER;
    }

    @Override
    public BLibEventRouter<BLibLevelTickEvent> postLevelTick() {
        return NeoForgeBLibLevelTickEvents.AFTER;
    }

    @Override
    public BLibEventRouter<BLibBlockBreakEvent> preBlockBreak() {
        return NeoForgeBLibPlayerBlockBreakEvents.BEFORE;
    }

    @Override
    public BLibEventRouter<BLibLevelTickEvent> preLevelTick() {
        return NeoForgeBLibLevelTickEvents.BEFORE;
    }
}
