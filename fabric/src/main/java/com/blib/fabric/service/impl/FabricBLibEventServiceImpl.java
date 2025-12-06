package com.blib.fabric.service.impl;

import com.blib.event.BLibEventRouter;
import com.blib.fabric.event.FabricBLibLevelTickEvents;
import com.blib.fabric.event.FabricBLibPlayerBlockBreakEvents;
import com.blib.fabric.event.FabricBLibTagsUpdatedEvents;
import com.blib.service.BLibEventService;

public class FabricBLibEventServiceImpl implements BLibEventService {

    @Override
    public BLibEventRouter<LevelTickEvent> afterLevelTick() {
        return FabricBLibLevelTickEvents.AFTER;
    }

    @Override
    public BLibEventRouter<BlockBreakEvent> beforeBlockBreak() {
        return FabricBLibPlayerBlockBreakEvents.BEFORE;
    }

    @Override
    public BLibEventRouter<LevelTickEvent> beforeLevelTick() {
        return FabricBLibLevelTickEvents.BEFORE;
    }

    @Override
    public BLibEventRouter<TagsUpdatedEvent> onTagsUpdated() {
        return FabricBLibTagsUpdatedEvents.ROUTER;
    }
}
