package com.blib.fabric.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;

import com.blib.common.event.BLibEventRouter;
import com.blib.common.event.BLibTagsUpdatedEvent;

public class FabricBLibTagsUpdatedEvents {

    public static final BLibEventRouter<BLibTagsUpdatedEvent> ROUTER = new BLibEventRouter<>() {

        private final BLibTagsUpdatedEvent dispatcher = (registryAccess, fromClientPacket) -> CommonLifecycleEvents.TAGS_LOADED
            .invoker()
            .onTagsLoaded(registryAccess, fromClientPacket);

        @Override
        public BLibTagsUpdatedEvent dispatcher() {
            return dispatcher;
        }

        @Override
        public void register(BLibTagsUpdatedEvent tagsUpdatedEvent) {
            CommonLifecycleEvents.TAGS_LOADED.register((tagsUpdatedEvent::invoke));
        }
    };
}
