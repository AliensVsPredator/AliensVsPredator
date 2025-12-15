package com.blib.internal.service;

import org.jetbrains.annotations.ApiStatus;

import com.blib.event.BLibBlockBreakEvent;
import com.blib.event.BLibEventRouter;
import com.blib.event.BLibLevelTickEvent;
import com.blib.event.BLibTagsUpdatedEvent;

@ApiStatus.Internal
public interface BLibEventService {

    BLibEventRouter<BLibLevelTickEvent> afterLevelTick();

    BLibEventRouter<BLibBlockBreakEvent> beforeBlockBreak();

    BLibEventRouter<BLibLevelTickEvent> beforeLevelTick();

    BLibEventRouter<BLibTagsUpdatedEvent> onTagsUpdated();

}
