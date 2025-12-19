package com.blib.fabric.internal.service.impl;

import org.jetbrains.annotations.ApiStatus;

import com.blib.common.event.BLibBlockBreakEvent;
import com.blib.common.event.BLibEventRouter;
import com.blib.common.event.BLibLevelTickEvent;
import com.blib.common.event.BLibPlayerTrackingEntityEvent;
import com.blib.common.event.BLibTagsUpdatedEvent;
import com.blib.fabric.internal.event.impl.FabricBLibLevelTickEvents;
import com.blib.fabric.internal.event.impl.FabricBLibPlayerBlockBreakEvents;
import com.blib.fabric.internal.event.impl.FabricBLibPlayerTrackingEntityEvents;
import com.blib.fabric.internal.event.impl.FabricBLibTagsUpdatedEvents;
import com.blib.internal.service.BLibEventService;

@ApiStatus.Internal
public class FabricBLibEventServiceImpl implements BLibEventService {

    @Override
    public BLibEventRouter<BLibPlayerTrackingEntityEvent> onPlayerStartTrackingEntity() {
        return FabricBLibPlayerTrackingEntityEvents.START;
    }

    @Override
    public BLibEventRouter<BLibTagsUpdatedEvent> onTagsUpdated() {
        return FabricBLibTagsUpdatedEvents.ROUTER;
    }

    @Override
    public BLibEventRouter<BLibLevelTickEvent> postLevelTick() {
        return FabricBLibLevelTickEvents.AFTER;
    }

    @Override
    public BLibEventRouter<BLibBlockBreakEvent> preBlockBreak() {
        return FabricBLibPlayerBlockBreakEvents.BEFORE;
    }

    @Override
    public BLibEventRouter<BLibLevelTickEvent> preLevelTick() {
        return FabricBLibLevelTickEvents.BEFORE;
    }
}
