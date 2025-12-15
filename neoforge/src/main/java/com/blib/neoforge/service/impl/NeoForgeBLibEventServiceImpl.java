package com.blib.neoforge.service.impl;

import com.blib.BLibMod;
import com.blib.event.BLibEventRouter;
import com.blib.internal.service.BLibEventService;
import com.blib.neoforge.event.impl.NeoForgeBLibLevelTickEvents;
import com.blib.neoforge.event.impl.NeoForgeBLibPlayerBlockBreakEvents;
import com.blib.neoforge.event.impl.NeoForgeBLibTagsUpdatedEvents;

public class NeoForgeBLibEventServiceImpl implements BLibEventService {

    public void finalize(BLibMod mod) {
        NeoForgeBLibLevelTickEvents.AFTER.initialize();
        NeoForgeBLibLevelTickEvents.BEFORE.initialize();
        NeoForgeBLibPlayerBlockBreakEvents.BEFORE.initialize();
        NeoForgeBLibTagsUpdatedEvents.ROUTER.initialize();
    }

    @Override
    public BLibEventRouter<LevelTickEvent> afterLevelTick() {
        return NeoForgeBLibLevelTickEvents.AFTER;
    }

    @Override
    public BLibEventRouter<BlockBreakEvent> beforeBlockBreak() {
        return NeoForgeBLibPlayerBlockBreakEvents.BEFORE;
    }

    @Override
    public BLibEventRouter<LevelTickEvent> beforeLevelTick() {
        return NeoForgeBLibLevelTickEvents.BEFORE;
    }

    @Override
    public BLibEventRouter<TagsUpdatedEvent> onTagsUpdated() {
        return NeoForgeBLibTagsUpdatedEvents.ROUTER;
    }
}
