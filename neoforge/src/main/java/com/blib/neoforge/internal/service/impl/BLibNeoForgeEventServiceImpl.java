package com.blib.neoforge.internal.service.impl;

import org.jetbrains.annotations.ApiStatus;

import com.blib.api.client.event.v1.BLibScreenInitEvent;
import com.blib.api.common.event.v1.BLibBlockBreakEvent;
import com.blib.api.common.event.v1.BLibChunkClaimAddedEvent;
import com.blib.api.common.event.v1.BLibChunkClaimRemovedEvent;
import com.blib.api.common.event.v1.BLibChunkLoadEvent;
import com.blib.api.common.event.v1.BLibChunkSaveEvent;
import com.blib.api.common.event.v1.BLibChunkUnloadEvent;
import com.blib.api.common.event.v1.BLibCommonSetupEvent;
import com.blib.api.common.event.v1.BLibEntityLoadEvent;
import com.blib.api.common.event.v1.BLibEntityRemoveEvent;
import com.blib.api.common.event.v1.BLibEntityTickEvent;
import com.blib.api.common.event.v1.BLibFactionCreatedEvent;
import com.blib.api.common.event.v1.BLibFactionDataChangedEvent;
import com.blib.api.common.event.v1.BLibFactionMemberChangedEvent;
import com.blib.api.common.event.v1.BLibFactionRelationshipChangedEvent;
import com.blib.api.common.event.v1.BLibFactionRemoveEvent;
import com.blib.api.common.event.v1.BLibFactionsLoadedEvent;
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
public class BLibNeoForgeEventServiceImpl implements BLibEventService {

    @Override
    public BLibEventListenerHandle<BLibChunkClaimAddedEvent> onChunkClaimAdded(BLibMod mod) {
        return BLibNeoForgeModContainerLookup.INSTANCE.get(mod)
            .onChunkClaimAdded();
    }

    @Override
    public BLibEventListenerHandle<BLibChunkClaimRemovedEvent> onChunkClaimRemoved(BLibMod mod) {
        return BLibNeoForgeModContainerLookup.INSTANCE.get(mod)
            .onChunkClaimRemoved();
    }

    @Override
    public BLibEventListenerHandle<BLibChunkLoadEvent> onChunkLoad(BLibMod mod) {
        return BLibNeoForgeModContainerLookup.INSTANCE.get(mod)
            .onChunkLoad();
    }

    @Override
    public BLibEventListenerHandle<BLibChunkSaveEvent> onChunkSave(BLibMod mod) {
        return BLibNeoForgeModContainerLookup.INSTANCE.get(mod)
            .onChunkSave();
    }

    @Override
    public BLibEventListenerHandle<BLibChunkUnloadEvent> onChunkUnload(BLibMod mod) {
        return BLibNeoForgeModContainerLookup.INSTANCE.get(mod)
            .onChunkUnload();
    }

    @Override
    public BLibEventListenerHandle<BLibCommonSetupEvent> onCommonSetup(BLibMod mod) {
        return BLibNeoForgeModContainerLookup.INSTANCE.get(mod)
            .onCommonSetup();
    }

    @Override
    public BLibEventListenerHandle<BLibEntityLoadEvent> onEntityLoad(BLibMod mod) {
        return BLibNeoForgeModContainerLookup.INSTANCE.get(mod)
            .onEntityLoad();
    }

    @Override
    public BLibEventListenerHandle<BLibEntityRemoveEvent> onEntityRemove(BLibMod mod) {
        return BLibNeoForgeModContainerLookup.INSTANCE.get(mod)
            .onEntityRemove();
    }

    @Override
    public BLibEventListenerHandle<BLibEntityTickEvent> onEntityTick(BLibMod mod) {
        return BLibNeoForgeModContainerLookup.INSTANCE.get(mod)
            .onEntityTick();
    }

    @Override
    public BLibEventListenerHandle<BLibFactionCreatedEvent> onFactionCreated(BLibMod mod) {
        return BLibNeoForgeModContainerLookup.INSTANCE.get(mod)
            .onFactionCreated();
    }

    @Override
    public BLibEventListenerHandle<BLibFactionDataChangedEvent> onFactionDataChanged(BLibMod mod) {
        return BLibNeoForgeModContainerLookup.INSTANCE.get(mod)
            .onFactionDataChanged();
    }

    @Override
    public BLibEventListenerHandle<BLibFactionMemberChangedEvent> onFactionMemberChanged(BLibMod mod) {
        return BLibNeoForgeModContainerLookup.INSTANCE.get(mod)
            .onFactionMemberChanged();
    }

    @Override
    public BLibEventListenerHandle<BLibFactionRelationshipChangedEvent> onFactionRelationshipChanged(BLibMod mod) {
        return BLibNeoForgeModContainerLookup.INSTANCE.get(mod)
            .onFactionRelationshipChanged();
    }

    @Override
    public BLibEventListenerHandle<BLibFactionRemoveEvent> onFactionRemove(BLibMod mod) {
        return BLibNeoForgeModContainerLookup.INSTANCE.get(mod)
            .onFactionRemove();
    }

    @Override
    public BLibEventListenerHandle<BLibFactionsLoadedEvent> onFactionsLoaded(BLibMod mod) {
        return BLibNeoForgeModContainerLookup.INSTANCE.get(mod)
            .onFactionsLoaded();
    }

    @Override
    public BLibEventListenerHandle<BLibLevelSaveEvent> onLevelSave(BLibMod mod) {
        return BLibNeoForgeModContainerLookup.INSTANCE.get(mod)
            .onLevelSave();
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
    public BLibEventHandle<BLibScreenInitEvent> postScreenInit(BLibMod mod) {
        return BLibNeoForgeModContainerLookup.INSTANCE.get(mod)
            .postScreenInit();
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

    @Override
    public BLibEventHandle<BLibServerLifecycleEvent.Started> onServerStarted(BLibMod mod) {
        return BLibNeoForgeModContainerLookup.INSTANCE.get(mod)
            .onServerStarted();
    }

    @Override
    public BLibEventHandle<BLibServerLifecycleEvent.Starting> onServerStarting(BLibMod mod) {
        return BLibNeoForgeModContainerLookup.INSTANCE.get(mod)
            .onServerStarting();
    }

    @Override
    public BLibEventHandle<BLibServerLifecycleEvent.Stopped> onServerStopped(BLibMod mod) {
        return BLibNeoForgeModContainerLookup.INSTANCE.get(mod)
            .onServerStopped();
    }

    @Override
    public BLibEventHandle<BLibServerLifecycleEvent.Stopping> onServerStopping(BLibMod mod) {
        return BLibNeoForgeModContainerLookup.INSTANCE.get(mod)
            .onServerStopping();
    }

    @Override
    public BLibEventListenerHandle<BLibServerSaveEvent> onServerSave(BLibMod mod) {
        return BLibNeoForgeModContainerLookup.INSTANCE.get(mod)
            .onServerSave();
    }
}
