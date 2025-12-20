package com.blib.neoforge.internal.service.impl;

import org.jetbrains.annotations.ApiStatus;

import com.blib.BLibMod;
import com.blib.common.event.BLibBlockBreakEvent;
import com.blib.common.event.BLibCommonSetupEvent;
import com.blib.common.event.BLibEventListenerHandle;
import com.blib.common.event.BLibEventRouter;
import com.blib.common.event.BLibLevelTickEvent;
import com.blib.common.event.BLibPlayerTrackingEntityEvent;
import com.blib.common.event.BLibTagsUpdatedEvent;
import com.blib.internal.service.BLibEventService;
import com.blib.neoforge.internal.event.impl.BLibNeoForgeLevelTickEvents;
import com.blib.neoforge.internal.event.impl.BLibNeoForgePlayerBlockBreakEvents;
import com.blib.neoforge.internal.event.impl.BLibNeoForgePlayerTrackingEntityEvents;
import com.blib.neoforge.internal.event.impl.BLibNeoForgeTagsUpdatedEvents;

@ApiStatus.Internal
public class BLibNeoForgeEventServiceImpl implements BLibEventService {

    @Override
    public BLibEventListenerHandle<BLibCommonSetupEvent> onCommonSetup(BLibMod mod) {
        return BLibNeoForgeModContainerLookup.INSTANCE.get(mod)
            .onCommonSetup();
    }

    @Override
    public BLibEventRouter<BLibPlayerTrackingEntityEvent> onPlayerStartTrackingEntity() {
        return BLibNeoForgePlayerTrackingEntityEvents.START;
    }

    @Override
    public BLibEventRouter<BLibTagsUpdatedEvent> onTagsUpdated() {
        return BLibNeoForgeTagsUpdatedEvents.ROUTER;
    }

    @Override
    public BLibEventRouter<BLibLevelTickEvent> postLevelTick() {
        return BLibNeoForgeLevelTickEvents.AFTER;
    }

    @Override
    public BLibEventRouter<BLibBlockBreakEvent> preBlockBreak() {
        return BLibNeoForgePlayerBlockBreakEvents.BEFORE;
    }

    @Override
    public BLibEventRouter<BLibLevelTickEvent> preLevelTick() {
        return BLibNeoForgeLevelTickEvents.BEFORE;
    }
}
