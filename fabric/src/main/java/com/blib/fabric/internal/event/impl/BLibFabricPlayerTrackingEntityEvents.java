package com.blib.fabric.internal.event.impl;

import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

import com.blib.BLibMod;
import com.blib.common.event.BLibEventHandle;
import com.blib.common.event.BLibPlayerTrackingEntityEvent;

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
        public void onRegister(BLibPlayerTrackingEntityEvent playerTrackingEntityEvent) {
            EntityTrackingEvents.START_TRACKING.register((playerTrackingEntityEvent::invoke));
        }
    };
}
