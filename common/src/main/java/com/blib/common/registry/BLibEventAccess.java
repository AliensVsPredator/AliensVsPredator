package com.blib.common.registry;

import com.blib.BLibMod;
import com.blib.event.BLibEventRouter;
import com.blib.internal.service.BLibEventService;
import com.blib.internal.service.BLibInternalServices;

public class BLibEventAccess {

    private final BLibMod mod;

    public BLibEventAccess(BLibMod mod) {
        this.mod = mod;
    }

    public BLibEventRouter<BLibEventService.LevelTickEvent> afterLevelTick() {
        return BLibInternalServices.EVENT.afterLevelTick();
    }

    public BLibEventRouter<BLibEventService.BlockBreakEvent> beforeBlockBreak() {
        return BLibInternalServices.EVENT.beforeBlockBreak();
    }

    public BLibEventRouter<BLibEventService.LevelTickEvent> beforeLevelTick() {
        return BLibInternalServices.EVENT.beforeLevelTick();
    }

    public BLibEventRouter<BLibEventService.TagsUpdatedEvent> onTagsUpdated() {
        return BLibInternalServices.EVENT.onTagsUpdated();
    }
}
