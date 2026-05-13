package com.blib.api.common.mod.v1.model.access;

import org.jetbrains.annotations.ApiStatus;

import com.blib.api.client.event.v1.BLibScreenInitEvent;
import com.blib.api.common.event.v1.BLibBlockBreakEvent;
import com.blib.api.common.event.v1.BLibChunkClaimAddedEvent;
import com.blib.api.common.event.v1.BLibChunkClaimRemovedEvent;
import com.blib.api.common.event.v1.BLibChunkLoadEvent;
import com.blib.api.common.event.v1.BLibChunkSaveEvent;
import com.blib.api.common.event.v1.BLibChunkUnloadEvent;
import com.blib.api.common.event.v1.BLibCommonSetupEvent;
import com.blib.api.common.event.v1.BLibEntityLoadEvent;
import com.blib.api.common.event.v1.BLibEntityRemoveEvent;
import com.blib.api.common.event.v1.BLibEntityTickEvent;
import com.blib.api.common.event.v1.BLibFactionRemoveEvent;
import com.blib.api.common.event.v1.BLibLevelSaveEvent;
import com.blib.api.common.event.v1.BLibLevelTickEvent;
import com.blib.api.common.event.v1.BLibPlayerTrackingEntityEvent;
import com.blib.api.common.event.v1.BLibServerLifecycleEvent;
import com.blib.api.common.event.v1.BLibServerSaveEvent;
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

    public BLibEventListenerHandle<BLibChunkClaimAddedEvent> onChunkClaimAdded() {
        return BLibInternalServices.EVENT.onChunkClaimAdded(mod);
    }

    public BLibEventListenerHandle<BLibChunkClaimRemovedEvent> onChunkClaimRemoved() {
        return BLibInternalServices.EVENT.onChunkClaimRemoved(mod);
    }

    public BLibEventListenerHandle<BLibChunkLoadEvent> onChunkLoad() {
        return BLibInternalServices.EVENT.onChunkLoad(mod);
    }

    public BLibEventListenerHandle<BLibChunkSaveEvent> onChunkSave() {
        return BLibInternalServices.EVENT.onChunkSave(mod);
    }

    public BLibEventListenerHandle<BLibChunkUnloadEvent> onChunkUnload() {
        return BLibInternalServices.EVENT.onChunkUnload(mod);
    }

    public BLibEventListenerHandle<BLibCommonSetupEvent> onCommonSetup() {
        return BLibInternalServices.EVENT.onCommonSetup(mod);
    }

    public BLibEventListenerHandle<BLibEntityLoadEvent> onEntityLoad() {
        return BLibInternalServices.EVENT.onEntityLoad(mod);
    }

    public BLibEventListenerHandle<BLibEntityRemoveEvent> onEntityRemove() {
        return BLibInternalServices.EVENT.onEntityRemove(mod);
    }

    public BLibEventListenerHandle<BLibEntityTickEvent> onEntityTick() {
        return BLibInternalServices.EVENT.onEntityTick(mod);
    }

    public BLibEventListenerHandle<BLibFactionRemoveEvent> onFactionRemove() {
        return BLibInternalServices.EVENT.onFactionRemove(mod);
    }

    public BLibEventListenerHandle<BLibLevelSaveEvent> onLevelSave() {
        return BLibInternalServices.EVENT.onLevelSave(mod);
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

    /**
     * Fires after a {@link net.minecraft.client.gui.screens.Screen}'s {@code init()} completes — useful for injecting
     * widgets into vanilla screens (e.g. an "Open BLib" button on the TitleScreen). Cross-loader wrapper around
     * Fabric's {@code ScreenEvents.AFTER_INIT} and NeoForge's {@code ScreenEvent.Init.Post}.
     */
    public BLibEventHandle<BLibScreenInitEvent> postScreenInit() {
        return BLibInternalServices.EVENT.postScreenInit(mod);
    }

    public BLibEventHandle<BLibBlockBreakEvent> preBlockBreak() {
        return BLibInternalServices.EVENT.preBlockBreak(mod);
    }

    public BLibEventHandle<BLibLevelTickEvent> preLevelTick() {
        return BLibInternalServices.EVENT.preLevelTick(mod);
    }

    public BLibEventListenerHandle<BLibServerSaveEvent> onServerSave() {
        return BLibInternalServices.EVENT.onServerSave(mod);
    }

    public BLibEventHandle<BLibServerLifecycleEvent.Started> onServerStarted() {
        return BLibInternalServices.EVENT.onServerStarted(mod);
    }

    public BLibEventHandle<BLibServerLifecycleEvent.Starting> onServerStarting() {
        return BLibInternalServices.EVENT.onServerStarting(mod);
    }

    public BLibEventHandle<BLibServerLifecycleEvent.Stopped> onServerStopped() {
        return BLibInternalServices.EVENT.onServerStopped(mod);
    }

    public BLibEventHandle<BLibServerLifecycleEvent.Stopping> onServerStopping() {
        return BLibInternalServices.EVENT.onServerStopping(mod);
    }

    /**
     * @deprecated
     */
    @Deprecated(forRemoval = true)
    public BLibEventHandle<BLibServerLifecycleEvent.Started> serverStarted() {
        return onServerStarted();
    }

    /**
     * @deprecated
     */
    @Deprecated(forRemoval = true)
    public BLibEventHandle<BLibServerLifecycleEvent.Starting> serverStarting() {
        return onServerStarting();
    }

    /**
     * @deprecated
     */
    @Deprecated(forRemoval = true)
    public BLibEventHandle<BLibServerLifecycleEvent.Stopped> serverStopped() {
        return onServerStopped();
    }

    /**
     * @deprecated
     */
    @Deprecated(forRemoval = true)
    public BLibEventHandle<BLibServerLifecycleEvent.Stopping> serverStopping() {
        return onServerStopping();
    }
}
