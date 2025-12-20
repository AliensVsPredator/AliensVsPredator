package com.blib.internal.service;

import org.jetbrains.annotations.ApiStatus;

import com.blib.BLibMod;
import com.blib.common.event.BLibBlockBreakEvent;
import com.blib.common.event.BLibCommonSetupEvent;
import com.blib.common.event.BLibEventListenerHandle;
import com.blib.common.event.BLibEventRouter;
import com.blib.common.event.BLibLevelTickEvent;
import com.blib.common.event.BLibPlayerTrackingEntityEvent;
import com.blib.common.event.BLibTagsUpdatedEvent;

@ApiStatus.Internal
public interface BLibEventService {

    BLibEventListenerHandle<BLibCommonSetupEvent> onCommonSetup(BLibMod mod);

    BLibEventRouter<BLibPlayerTrackingEntityEvent> onPlayerStartTrackingEntity(BLibMod mod);

    BLibEventRouter<BLibTagsUpdatedEvent> onTagsUpdated(BLibMod mod);

    BLibEventRouter<BLibLevelTickEvent> postLevelTick(BLibMod mod);

    BLibEventRouter<BLibBlockBreakEvent> preBlockBreak(BLibMod mod);

    BLibEventRouter<BLibLevelTickEvent> preLevelTick(BLibMod mod);
}
