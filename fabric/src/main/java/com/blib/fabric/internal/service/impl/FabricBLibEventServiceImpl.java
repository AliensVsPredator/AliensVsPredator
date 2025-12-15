package com.blib.fabric.internal.service.impl;

import org.jetbrains.annotations.ApiStatus;

import com.blib.common.event.BLibBlockBreakEvent;
import com.blib.common.event.BLibEventRouter;
import com.blib.common.event.BLibLevelTickEvent;
import com.blib.common.event.BLibTagsUpdatedEvent;
import com.blib.fabric.internal.event.FabricBLibLevelTickEvents;
import com.blib.fabric.internal.event.FabricBLibPlayerBlockBreakEvents;
import com.blib.fabric.internal.event.FabricBLibTagsUpdatedEvents;
import com.blib.internal.service.BLibEventService;

@ApiStatus.Internal
public class FabricBLibEventServiceImpl implements BLibEventService {

    @Override
    public BLibEventRouter<BLibLevelTickEvent> afterLevelTick() {
        return FabricBLibLevelTickEvents.AFTER;
    }

    @Override
    public BLibEventRouter<BLibBlockBreakEvent> beforeBlockBreak() {
        return FabricBLibPlayerBlockBreakEvents.BEFORE;
    }

    @Override
    public BLibEventRouter<BLibLevelTickEvent> beforeLevelTick() {
        return FabricBLibLevelTickEvents.BEFORE;
    }

    @Override
    public BLibEventRouter<BLibTagsUpdatedEvent> onTagsUpdated() {
        return FabricBLibTagsUpdatedEvents.ROUTER;
    }
}
