package com.blib.internal.service;

import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.event.v1.BLibBlockBreakEvent;
import com.blib.api.common.event.v1.BLibChunkSaveEvent;
import com.blib.api.common.event.v1.BLibChunkUnloadEvent;
import com.blib.api.common.event.v1.BLibCommonSetupEvent;
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

@ApiStatus.Internal
public interface BLibEventService {

    BLibEventListenerHandle<BLibChunkSaveEvent> onChunkSave(BLibMod mod);

    BLibEventListenerHandle<BLibChunkUnloadEvent> onChunkUnload(BLibMod mod);

    BLibEventListenerHandle<BLibCommonSetupEvent> onCommonSetup(BLibMod mod);

    BLibEventListenerHandle<BLibEntityRemoveEvent> onEntityRemove(BLibMod mod);

    BLibEventListenerHandle<BLibEntityTickEvent> onEntityTick(BLibMod mod);

    BLibEventListenerHandle<BLibFactionRemoveEvent> onFactionRemove(BLibMod mod);

    BLibEventListenerHandle<BLibLevelSaveEvent> onLevelSave(BLibMod mod);

    BLibEventHandle<BLibPlayerTrackingEntityEvent> onPlayerStartTrackingEntity(BLibMod mod);

    BLibEventListenerHandle<BLibServerSaveEvent> onServerSave(BLibMod mod);

    BLibEventHandle<BLibTagsUpdatedEvent> onTagsUpdated(BLibMod mod);

    BLibEventHandle<BLibLevelTickEvent> postLevelTick(BLibMod mod);

    BLibEventHandle<BLibBlockBreakEvent> preBlockBreak(BLibMod mod);

    BLibEventHandle<BLibLevelTickEvent> preLevelTick(BLibMod mod);

    BLibEventHandle<BLibServerLifecycleEvent.Started> onServerStarted(BLibMod mod);

    BLibEventHandle<BLibServerLifecycleEvent.Starting> onServerStarting(BLibMod mod);

    BLibEventHandle<BLibServerLifecycleEvent.Stopped> onServerStopped(BLibMod mod);

    BLibEventHandle<BLibServerLifecycleEvent.Stopping> onServerStopping(BLibMod mod);
}
