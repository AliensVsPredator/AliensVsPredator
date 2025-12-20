package com.blib.fabric.internal.service.impl;

import org.jetbrains.annotations.ApiStatus;

import com.blib.BLibMod;
import com.blib.common.event.BLibBlockBreakEvent;
import com.blib.common.event.BLibCommonSetupEvent;
import com.blib.common.event.BLibEventListenerHandle;
import com.blib.common.event.BLibEventRouter;
import com.blib.common.event.BLibLevelTickEvent;
import com.blib.common.event.BLibPlayerTrackingEntityEvent;
import com.blib.common.event.BLibTagsUpdatedEvent;
import com.blib.fabric.internal.event.impl.BLibFabricLevelTickEvents;
import com.blib.fabric.internal.event.impl.BLibFabricPlayerBlockBreakEvents;
import com.blib.fabric.internal.event.impl.BLibFabricPlayerTrackingEntityEvents;
import com.blib.fabric.internal.event.impl.BLibFabricTagsUpdatedEvents;
import com.blib.internal.service.BLibEventService;

@ApiStatus.Internal
public class BLibFabricEventServiceImpl implements BLibEventService {

    @Override
    public BLibEventListenerHandle<BLibCommonSetupEvent> onCommonSetup(BLibMod mod) {
        return BLibFabricModContainerLookup.INSTANCE.get(mod)
            .onCommonSetup();
    }

    @Override
    public BLibEventRouter<BLibPlayerTrackingEntityEvent> onPlayerStartTrackingEntity() {
        return BLibFabricPlayerTrackingEntityEvents.START;
    }

    @Override
    public BLibEventRouter<BLibTagsUpdatedEvent> onTagsUpdated() {
        return BLibFabricTagsUpdatedEvents.ROUTER;
    }

    @Override
    public BLibEventRouter<BLibLevelTickEvent> postLevelTick() {
        return BLibFabricLevelTickEvents.AFTER;
    }

    @Override
    public BLibEventRouter<BLibBlockBreakEvent> preBlockBreak() {
        return BLibFabricPlayerBlockBreakEvents.BEFORE;
    }

    @Override
    public BLibEventRouter<BLibLevelTickEvent> preLevelTick() {
        return BLibFabricLevelTickEvents.BEFORE;
    }
}
