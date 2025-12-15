package com.blib.fabric.service.impl;

import com.blib.common.event.BLibBlockBreakEvent;
import com.blib.common.event.BLibEventRouter;
import com.blib.common.event.BLibLevelTickEvent;
import com.blib.common.event.BLibTagsUpdatedEvent;
import com.blib.fabric.event.FabricBLibLevelTickEvents;
import com.blib.fabric.event.FabricBLibPlayerBlockBreakEvents;
import com.blib.fabric.event.FabricBLibTagsUpdatedEvents;
import com.blib.internal.service.BLibEventService;

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
