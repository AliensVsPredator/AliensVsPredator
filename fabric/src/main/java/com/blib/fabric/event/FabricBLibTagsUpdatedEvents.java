package com.blib.fabric.event;

import com.blib.event.BLibEventRouter;
import com.blib.service.BLibEventService;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;

public class FabricBLibTagsUpdatedEvents {

    public static final BLibEventRouter<BLibEventService.TagsUpdatedEvent> ROUTER = new BLibEventRouter<>() {

        private final BLibEventService.TagsUpdatedEvent dispatcher = (registryAccess, fromClientPacket) ->
            CommonLifecycleEvents.TAGS_LOADED.invoker().onTagsLoaded(registryAccess, fromClientPacket);

        @Override
        public BLibEventService.TagsUpdatedEvent dispatcher() {
            return dispatcher;
        }

        @Override
        public void register(BLibEventService.TagsUpdatedEvent tagsUpdatedEvent) {
            CommonLifecycleEvents.TAGS_LOADED.register((tagsUpdatedEvent::invoke));
        }
    };
}
