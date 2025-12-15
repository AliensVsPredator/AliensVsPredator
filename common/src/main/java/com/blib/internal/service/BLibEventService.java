package com.blib.internal.service;

import org.jetbrains.annotations.ApiStatus;

import com.blib.common.event.BLibBlockBreakEvent;
import com.blib.common.event.BLibEventRouter;
import com.blib.common.event.BLibLevelTickEvent;
import com.blib.common.event.BLibTagsUpdatedEvent;

@ApiStatus.Internal
public interface BLibEventService {

    BLibEventRouter<BLibLevelTickEvent> afterLevelTick();

    BLibEventRouter<BLibBlockBreakEvent> beforeBlockBreak();

    BLibEventRouter<BLibLevelTickEvent> beforeLevelTick();

    BLibEventRouter<BLibTagsUpdatedEvent> onTagsUpdated();

}
