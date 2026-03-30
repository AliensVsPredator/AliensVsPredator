package com.blib.fabric.internal.service.impl;

import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.event.v1.BLibBlockBreakEvent;
import com.blib.api.common.event.v1.BLibChunkSaveEvent;
import com.blib.api.common.event.v1.BLibChunkUnloadEvent;
import com.blib.api.common.event.v1.BLibCommonSetupEvent;
import com.blib.api.common.event.v1.BLibEntityLoadEvent;
import com.blib.api.common.event.v1.BLibEntityRemoveEvent;
import com.blib.api.common.event.v1.BLibEntityTickEvent;
import com.blib.api.common.event.v1.BLibFactionRemoveEvent;
import com.blib.api.common.event.v1.BLibLevelSaveEvent;
import com.blib.api.common.event.v1.BLibLevelTickEvent;
import com.blib.api.common.event.v1.BLibPlayerTrackingEntityEvent;
import com.blib.api.common.event.v1.BLibServerLifecycleEvent;
import com.blib.api.common.event.v1.BLibServerSaveEvent;
import com.blib.api.common.event.v1.BLibTagsUpdatedEvent;
import com.blib.api.common.event.v1.handle.BLibEventHandle;
import com.blib.api.common.event.v1.handle.BLibEventListenerHandle;
import com.blib.api.common.mod.v1.BLibMod;
import com.blib.internal.service.BLibEventService;

@ApiStatus.Internal
public class BLibFabricEventServiceImpl implements BLibEventService {

    @Override
    public BLibEventListenerHandle<BLibChunkSaveEvent> onChunkSave(BLibMod mod) {
        return BLibFabricModContainerLookup.INSTANCE.get(mod)
            .onChunkSave();
    }

    @Override
    public BLibEventListenerHandle<BLibChunkUnloadEvent> onChunkUnload(BLibMod mod) {
        return BLibFabricModContainerLookup.INSTANCE.get(mod)
            .onChunkUnload();
    }

    @Override
    public BLibEventListenerHandle<BLibCommonSetupEvent> onCommonSetup(BLibMod mod) {
        return BLibFabricModContainerLookup.INSTANCE.get(mod)
            .onCommonSetup();
    }

    @Override
    public BLibEventListenerHandle<BLibEntityLoadEvent> onEntityLoad(BLibMod mod) {
        return BLibFabricModContainerLookup.INSTANCE.get(mod)
            .onEntityLoad();
    }

    @Override
    public BLibEventListenerHandle<BLibEntityRemoveEvent> onEntityRemove(BLibMod mod) {
        return BLibFabricModContainerLookup.INSTANCE.get(mod)
            .onEntityRemove();
    }

    @Override
    public BLibEventListenerHandle<BLibEntityTickEvent> onEntityTick(BLibMod mod) {
        return BLibFabricModContainerLookup.INSTANCE.get(mod)
            .onEntityTick();
    }

    @Override
    public BLibEventListenerHandle<BLibFactionRemoveEvent> onFactionRemove(BLibMod mod) {
        return BLibFabricModContainerLookup.INSTANCE.get(mod)
            .onFactionRemove();
    }

    @Override
    public BLibEventListenerHandle<BLibLevelSaveEvent> onLevelSave(BLibMod mod) {
        return BLibFabricModContainerLookup.INSTANCE.get(mod)
            .onLevelSave();
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
    public BLibEventHandle<BLibServerLifecycleEvent.Started> onServerStarted(BLibMod mod) {
        return BLibFabricModContainerLookup.INSTANCE.get(mod)
            .onServerStarted();
    }

    @Override
    public BLibEventHandle<BLibServerLifecycleEvent.Starting> onServerStarting(BLibMod mod) {
        return BLibFabricModContainerLookup.INSTANCE.get(mod)
            .onServerStarting();
    }

    @Override
    public BLibEventHandle<BLibServerLifecycleEvent.Stopped> onServerStopped(BLibMod mod) {
        return BLibFabricModContainerLookup.INSTANCE.get(mod)
            .onServerStopped();
    }

    @Override
    public BLibEventHandle<BLibServerLifecycleEvent.Stopping> onServerStopping(BLibMod mod) {
        return BLibFabricModContainerLookup.INSTANCE.get(mod)
            .onServerStopping();
    }

    @Override
    public BLibEventListenerHandle<BLibServerSaveEvent> onServerSave(BLibMod mod) {
        return BLibFabricModContainerLookup.INSTANCE.get(mod)
            .onServerSave();
    }
}
