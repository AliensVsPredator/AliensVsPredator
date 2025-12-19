package com.blib.fabric.internal.event;

import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;

import com.blib.common.event.BLibEventRouter;
import com.blib.common.event.BLibPlayerTrackingEntityEvent;

@ApiStatus.Internal
public final class FabricBLibPlayerTrackingEntityEvents {

    public static final BLibEventRouter<BLibPlayerTrackingEntityEvent> START = new BLibEventRouter<>() {

        private final BLibPlayerTrackingEntityEvent dispatcher = (trackedEntity, player) -> EntityTrackingEvents.START_TRACKING
            .invoker()
            .onStartTracking(trackedEntity, (ServerPlayer) player);

        @Override
        public BLibPlayerTrackingEntityEvent dispatcher() {
            return dispatcher;
        }

        @Override
        public void register(BLibPlayerTrackingEntityEvent playerTrackingEntityEvent) {
            EntityTrackingEvents.START_TRACKING.register((playerTrackingEntityEvent::invoke));
        }
    };
}
