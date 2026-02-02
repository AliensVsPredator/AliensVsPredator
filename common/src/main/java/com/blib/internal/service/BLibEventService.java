package com.blib.internal.service;

import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.event.v1.BLibBlockBreakEvent;
import com.blib.api.common.event.v1.BLibCommonSetupEvent;
import com.blib.api.common.event.v1.BLibEntityTickEvent;
import com.blib.api.common.event.v1.BLibLevelTickEvent;
import com.blib.api.common.event.v1.BLibPlayerTrackingEntityEvent;
import com.blib.api.common.event.v1.BLibServerLifecycleEvent;
import com.blib.api.common.event.v1.BLibTagsUpdatedEvent;
import com.blib.api.common.event.v1.handle.BLibEventHandle;
import com.blib.api.common.event.v1.handle.BLibEventListenerHandle;
import com.blib.api.common.mod.v1.BLibMod;

@ApiStatus.Internal
public interface BLibEventService {

    BLibEventListenerHandle<BLibCommonSetupEvent> onCommonSetup(BLibMod mod);

    BLibEventListenerHandle<BLibEntityTickEvent> onEntityTick(BLibMod mod);

    BLibEventHandle<BLibPlayerTrackingEntityEvent> onPlayerStartTrackingEntity(BLibMod mod);

    BLibEventHandle<BLibTagsUpdatedEvent> onTagsUpdated(BLibMod mod);

    BLibEventHandle<BLibLevelTickEvent> postLevelTick(BLibMod mod);

    BLibEventHandle<BLibBlockBreakEvent> preBlockBreak(BLibMod mod);

    BLibEventHandle<BLibLevelTickEvent> preLevelTick(BLibMod mod);

    BLibEventHandle<BLibServerLifecycleEvent.Started> serverStarted(BLibMod mod);

    BLibEventHandle<BLibServerLifecycleEvent.Starting> serverStarting(BLibMod mod);

    BLibEventHandle<BLibServerLifecycleEvent.Stopped> serverStopped(BLibMod mod);

    BLibEventHandle<BLibServerLifecycleEvent.Stopping> serverStopping(BLibMod mod);
}
