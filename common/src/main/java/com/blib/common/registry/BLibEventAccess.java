package com.blib.common.registry;

import com.blib.BLibMod;
import com.blib.event.BLibBlockBreakEvent;
import com.blib.event.BLibEventRouter;
import com.blib.event.BLibLevelTickEvent;
import com.blib.event.BLibTagsUpdatedEvent;
import com.blib.internal.service.BLibInternalServices;

public class BLibEventAccess {

    private final BLibMod mod;

    public BLibEventAccess(BLibMod mod) {
        this.mod = mod;
    }

    public BLibEventRouter<BLibLevelTickEvent> afterLevelTick() {
        return BLibInternalServices.EVENT.afterLevelTick();
    }

    public BLibEventRouter<BLibBlockBreakEvent> beforeBlockBreak() {
        return BLibInternalServices.EVENT.beforeBlockBreak();
    }

    public BLibEventRouter<BLibLevelTickEvent> beforeLevelTick() {
        return BLibInternalServices.EVENT.beforeLevelTick();
    }

    public BLibEventRouter<BLibTagsUpdatedEvent> onTagsUpdated() {
        return BLibInternalServices.EVENT.onTagsUpdated();
    }
}
