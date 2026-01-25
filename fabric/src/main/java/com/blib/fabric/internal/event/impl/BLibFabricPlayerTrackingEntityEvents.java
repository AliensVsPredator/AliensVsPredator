package com.blib.fabric.internal.event.impl;

import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

import com.blib.api.common.event.v1.BLibPlayerTrackingEntityEvent;
import com.blib.api.common.event.v1.handle.BLibEventHandle;
import com.blib.api.common.mod.v1.BLibMod;

@ApiStatus.Internal
public final class BLibFabricPlayerTrackingEntityEvents {

    public static final Function<BLibMod, BLibEventHandle<BLibPlayerTrackingEntityEvent>> FACTORY = mod -> new BLibEventHandle<>(mod) {

        private final BLibPlayerTrackingEntityEvent dispatcher = (trackedEntity, player) -> EntityTrackingEvents.START_TRACKING
            .invoker()
            .onStartTracking(trackedEntity, (ServerPlayer) player);

        @Override
        public BLibPlayerTrackingEntityEvent dispatcher() {
            return dispatcher;
        }

        @Override
        public void onRegister(BLibPlayerTrackingEntityEvent event) {
            EntityTrackingEvents.START_TRACKING.register((event::invoke));
        }
    };
}
