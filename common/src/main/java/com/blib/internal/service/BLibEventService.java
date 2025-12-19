package com.blib.internal.service;

import org.jetbrains.annotations.ApiStatus;

import com.blib.common.event.BLibBlockBreakEvent;
import com.blib.common.event.BLibEventRouter;
import com.blib.common.event.BLibLevelTickEvent;
import com.blib.common.event.BLibPlayerTrackingEntityEvent;
import com.blib.common.event.BLibTagsUpdatedEvent;

@ApiStatus.Internal
public interface BLibEventService {

    BLibEventRouter<BLibPlayerTrackingEntityEvent> onPlayerStartTrackingEntity();

    BLibEventRouter<BLibTagsUpdatedEvent> onTagsUpdated();

    BLibEventRouter<BLibLevelTickEvent> postLevelTick();

    BLibEventRouter<BLibBlockBreakEvent> preBlockBreak();

    BLibEventRouter<BLibLevelTickEvent> preLevelTick();
}
