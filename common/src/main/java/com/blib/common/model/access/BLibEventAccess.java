package com.blib.common.model.access;

import org.jetbrains.annotations.ApiStatus;

import com.blib.BLibMod;
import com.blib.common.event.BLibBlockBreakEvent;
import com.blib.common.event.BLibCommonSetupEvent;
import com.blib.common.event.BLibEventHandle;
import com.blib.common.event.BLibEventListenerHandle;
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

    public BLibEventListenerHandle<BLibCommonSetupEvent> onCommonSetup() {
        return BLibInternalServices.EVENT.onCommonSetup(mod);
    }

    public BLibEventHandle<BLibPlayerTrackingEntityEvent> onPlayerStartTrackingEntity() {
        return BLibInternalServices.EVENT.onPlayerStartTrackingEntity(mod);
    }

    public BLibEventHandle<BLibTagsUpdatedEvent> onTagsUpdated() {
        return BLibInternalServices.EVENT.onTagsUpdated(mod);
    }

    public BLibEventHandle<BLibLevelTickEvent> postLevelTick() {
        return BLibInternalServices.EVENT.postLevelTick(mod);
    }

    public BLibEventHandle<BLibBlockBreakEvent> preBlockBreak() {
        return BLibInternalServices.EVENT.preBlockBreak(mod);
    }

    public BLibEventHandle<BLibLevelTickEvent> preLevelTick() {
        return BLibInternalServices.EVENT.preLevelTick(mod);
    }
}
