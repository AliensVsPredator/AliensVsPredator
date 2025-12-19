package com.blib.common.model.access;

import org.jetbrains.annotations.ApiStatus;

import com.blib.BLibMod;
import com.blib.common.event.BLibBlockBreakEvent;
import com.blib.common.event.BLibEventRouter;
import com.blib.common.event.BLibLevelTickEvent;
import com.blib.common.event.BLibPlayerTrackingEntityEvent;
import com.blib.common.event.BLibTagsUpdatedEvent;
import com.blib.internal.service.BLibInternalServices;

public class BLibEventAccess {

    private final BLibMod mod;

    @ApiStatus.Internal
    public BLibEventAccess(BLibMod mod) {
        this.mod = mod;
    }

    public BLibEventRouter<BLibPlayerTrackingEntityEvent> onPlayerStartTrackingEntity() {
        return BLibInternalServices.EVENT.onPlayerStartTrackingEntity();
    }

    public BLibEventRouter<BLibTagsUpdatedEvent> onTagsUpdated() {
        return BLibInternalServices.EVENT.onTagsUpdated();
    }

    public BLibEventRouter<BLibLevelTickEvent> postLevelTick() {
        return BLibInternalServices.EVENT.postLevelTick();
    }

    public BLibEventRouter<BLibBlockBreakEvent> preBlockBreak() {
        return BLibInternalServices.EVENT.preBlockBreak();
    }

    public BLibEventRouter<BLibLevelTickEvent> preLevelTick() {
        return BLibInternalServices.EVENT.preLevelTick();
    }
}
