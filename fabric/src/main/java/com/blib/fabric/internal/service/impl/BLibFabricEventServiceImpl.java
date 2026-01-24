package com.blib.fabric.internal.service.impl;

import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.event.v1.BLibBlockBreakEvent;
import com.blib.api.common.event.v1.BLibCommonSetupEvent;
import com.blib.api.common.event.v1.BLibLevelTickEvent;
import com.blib.api.common.event.v1.BLibPlayerTrackingEntityEvent;
import com.blib.api.common.event.v1.BLibServerLifecycleEvent;
import com.blib.api.common.event.v1.BLibTagsUpdatedEvent;
import com.blib.api.common.event.v1.handle.BLibEventHandle;
import com.blib.api.common.event.v1.handle.BLibEventListenerHandle;
import com.blib.api.common.mod.v1.BLibMod;
import com.blib.internal.service.BLibEventService;

@ApiStatus.Internal
public class BLibFabricEventServiceImpl implements BLibEventService {

    @Override
    public BLibEventListenerHandle<BLibCommonSetupEvent> onCommonSetup(BLibMod mod) {
        return BLibFabricModContainerLookup.INSTANCE.get(mod)
            .onCommonSetup();
    }

    @Override
    public BLibEventHandle<BLibPlayerTrackingEntityEvent> onPlayerStartTrackingEntity(BLibMod mod) {
        return BLibFabricModContainerLookup.INSTANCE.get(mod)
            .onPlayerStartTrackingEntity();
    }

    @Override
    public BLibEventHandle<BLibTagsUpdatedEvent> onTagsUpdated(BLibMod mod) {
        return BLibFabricModContainerLookup.INSTANCE.get(mod)
            .onTagsUpdated();
    }

    @Override
    public BLibEventHandle<BLibLevelTickEvent> postLevelTick(BLibMod mod) {
        return BLibFabricModContainerLookup.INSTANCE.get(mod)
            .postLevelTick();
    }

    @Override
    public BLibEventHandle<BLibBlockBreakEvent> preBlockBreak(BLibMod mod) {
        return BLibFabricModContainerLookup.INSTANCE.get(mod)
            .preBlockBreak();
    }

    @Override
    public BLibEventHandle<BLibLevelTickEvent> preLevelTick(BLibMod mod) {
        return BLibFabricModContainerLookup.INSTANCE.get(mod)
            .preLevelTick();
    }

    @Override
    public BLibEventHandle<BLibServerLifecycleEvent.Started> serverStarted(BLibMod mod) {
        return BLibFabricModContainerLookup.INSTANCE.get(mod)
            .serverStarted();
    }

    @Override
    public BLibEventHandle<BLibServerLifecycleEvent.Starting> serverStarting(BLibMod mod) {
        return BLibFabricModContainerLookup.INSTANCE.get(mod)
            .serverStarting();
    }

    @Override
    public BLibEventHandle<BLibServerLifecycleEvent.Stopped> serverStopped(BLibMod mod) {
        return BLibFabricModContainerLookup.INSTANCE.get(mod)
            .serverStopped();
    }

    @Override
    public BLibEventHandle<BLibServerLifecycleEvent.Stopping> serverStopping(BLibMod mod) {
        return BLibFabricModContainerLookup.INSTANCE.get(mod)
            .serverStopping();
    }
}
