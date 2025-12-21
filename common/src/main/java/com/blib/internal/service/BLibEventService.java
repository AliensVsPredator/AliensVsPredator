package com.blib.internal.service;

import org.jetbrains.annotations.ApiStatus;

import com.blib.BLibMod;
import com.blib.common.event.BLibBlockBreakEvent;
import com.blib.common.event.BLibCommonSetupEvent;
import com.blib.common.event.BLibEventHandle;
import com.blib.common.event.BLibEventListenerHandle;
import com.blib.common.event.BLibLevelTickEvent;
import com.blib.common.event.BLibPlayerTrackingEntityEvent;
import com.blib.common.event.BLibTagsUpdatedEvent;

@ApiStatus.Internal
public interface BLibEventService {

    BLibEventListenerHandle<BLibCommonSetupEvent> onCommonSetup(BLibMod mod);

    BLibEventHandle<BLibPlayerTrackingEntityEvent> onPlayerStartTrackingEntity(BLibMod mod);

    BLibEventHandle<BLibTagsUpdatedEvent> onTagsUpdated(BLibMod mod);

    BLibEventHandle<BLibLevelTickEvent> postLevelTick(BLibMod mod);

    BLibEventHandle<BLibBlockBreakEvent> preBlockBreak(BLibMod mod);

    BLibEventHandle<BLibLevelTickEvent> preLevelTick(BLibMod mod);
}
