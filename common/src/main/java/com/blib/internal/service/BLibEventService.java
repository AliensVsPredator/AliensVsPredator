package com.blib.internal.service;

import org.jetbrains.annotations.ApiStatus;

import com.blib.BLibMod;
import com.blib.common.event.BLibBlockBreakEvent;
import com.blib.common.event.BLibCommonSetupEvent;
import com.blib.common.event.BLibLevelTickEvent;
import com.blib.common.event.BLibPlayerTrackingEntityEvent;
import com.blib.common.event.BLibServerLifecycleEvent;
import com.blib.common.event.BLibTagsUpdatedEvent;
import com.blib.common.event.handle.BLibEventHandle;
import com.blib.common.event.handle.BLibEventListenerHandle;

@ApiStatus.Internal
public interface BLibEventService {

    BLibEventListenerHandle<BLibCommonSetupEvent> onCommonSetup(BLibMod mod);

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
