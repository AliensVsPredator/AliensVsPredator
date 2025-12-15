package com.blib.neoforge.service.impl;

import com.blib.BLibMod;
import com.blib.common.event.BLibBlockBreakEvent;
import com.blib.common.event.BLibEventRouter;
import com.blib.common.event.BLibLevelTickEvent;
import com.blib.common.event.BLibTagsUpdatedEvent;
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
    public BLibEventRouter<BLibLevelTickEvent> afterLevelTick() {
        return NeoForgeBLibLevelTickEvents.AFTER;
    }

    @Override
    public BLibEventRouter<BLibBlockBreakEvent> beforeBlockBreak() {
        return NeoForgeBLibPlayerBlockBreakEvents.BEFORE;
    }

    @Override
    public BLibEventRouter<BLibLevelTickEvent> beforeLevelTick() {
        return NeoForgeBLibLevelTickEvents.BEFORE;
    }

    @Override
    public BLibEventRouter<BLibTagsUpdatedEvent> onTagsUpdated() {
        return NeoForgeBLibTagsUpdatedEvents.ROUTER;
    }
}
