package com.blib.neoforge.internal.service.impl;

import org.jetbrains.annotations.ApiStatus;

import com.blib.BLibMod;
import com.blib.common.event.BLibBlockBreakEvent;
import com.blib.common.event.BLibCommonSetupEvent;
import com.blib.common.event.BLibLevelTickEvent;
import com.blib.common.event.BLibPlayerTrackingEntityEvent;
import com.blib.common.event.BLibTagsUpdatedEvent;
import com.blib.common.event.handle.BLibEventHandle;
import com.blib.common.event.handle.BLibEventListenerHandle;
import com.blib.internal.service.BLibEventService;

@ApiStatus.Internal
public class BLibNeoForgeEventServiceImpl implements BLibEventService {

    @Override
    public BLibEventListenerHandle<BLibCommonSetupEvent> onCommonSetup(BLibMod mod) {
        return BLibNeoForgeModContainerLookup.INSTANCE.get(mod)
            .onCommonSetup();
    }

    @Override
    public BLibEventHandle<BLibPlayerTrackingEntityEvent> onPlayerStartTrackingEntity(BLibMod mod) {
        return BLibNeoForgeModContainerLookup.INSTANCE.get(mod)
            .onPlayerStartTrackingEntity();
    }

    @Override
    public BLibEventHandle<BLibTagsUpdatedEvent> onTagsUpdated(BLibMod mod) {
        return BLibNeoForgeModContainerLookup.INSTANCE.get(mod)
            .onTagsUpdated();
    }

    @Override
    public BLibEventHandle<BLibLevelTickEvent> postLevelTick(BLibMod mod) {
        return BLibNeoForgeModContainerLookup.INSTANCE.get(mod)
            .postLevelTick();
    }

    @Override
    public BLibEventHandle<BLibBlockBreakEvent> preBlockBreak(BLibMod mod) {
        return BLibNeoForgeModContainerLookup.INSTANCE.get(mod)
            .preBlockBreak();
    }

    @Override
    public BLibEventHandle<BLibLevelTickEvent> preLevelTick(BLibMod mod) {
        return BLibNeoForgeModContainerLookup.INSTANCE.get(mod)
            .preLevelTick();
    }
}
