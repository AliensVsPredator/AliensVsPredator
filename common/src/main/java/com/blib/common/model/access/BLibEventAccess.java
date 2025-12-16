package com.blib.common.model.access;

import org.jetbrains.annotations.ApiStatus;

import com.blib.BLibMod;
import com.blib.common.event.BLibBlockBreakEvent;
import com.blib.common.event.BLibEventRouter;
import com.blib.common.event.BLibLevelTickEvent;
import com.blib.common.event.BLibTagsUpdatedEvent;
import com.blib.internal.service.BLibInternalServices;

public class BLibEventAccess {

    private final BLibMod mod;

    @ApiStatus.Internal
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
