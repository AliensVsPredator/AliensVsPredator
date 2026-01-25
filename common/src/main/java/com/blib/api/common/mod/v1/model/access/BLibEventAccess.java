package com.blib.api.common.mod.v1.model.access;

import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.event.v1.BLibBlockBreakEvent;
import com.blib.api.common.event.v1.BLibCommonSetupEvent;
import com.blib.api.common.event.v1.BLibLevelTickEvent;
import com.blib.api.common.event.v1.BLibPlayerTrackingEntityEvent;
import com.blib.api.common.event.v1.BLibServerLifecycleEvent;
import com.blib.api.common.event.v1.BLibTagsUpdatedEvent;
import com.blib.api.common.event.v1.handle.BLibEventHandle;
import com.blib.api.common.event.v1.handle.BLibEventListenerHandle;
import com.blib.api.common.mod.v1.BLibMod;
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

    public BLibEventHandle<BLibServerLifecycleEvent.Started> serverStarted() {
        return BLibInternalServices.EVENT.serverStarted(mod);
    }

    public BLibEventHandle<BLibServerLifecycleEvent.Starting> serverStarting() {
        return BLibInternalServices.EVENT.serverStarting(mod);
    }

    public BLibEventHandle<BLibServerLifecycleEvent.Stopped> serverStopped() {
        return BLibInternalServices.EVENT.serverStopped(mod);
    }

    public BLibEventHandle<BLibServerLifecycleEvent.Stopping> serverStopping() {
        return BLibInternalServices.EVENT.serverStopping(mod);
    }
}
