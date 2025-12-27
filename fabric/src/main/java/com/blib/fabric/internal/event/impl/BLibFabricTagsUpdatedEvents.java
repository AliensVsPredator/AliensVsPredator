package com.blib.fabric.internal.event.impl;

import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

import com.blib.BLibMod;
import com.blib.common.event.BLibTagsUpdatedEvent;
import com.blib.common.event.handle.BLibEventHandle;

@ApiStatus.Internal
public final class BLibFabricTagsUpdatedEvents {

    public static final Function<BLibMod, BLibEventHandle<BLibTagsUpdatedEvent>> FACTORY = mod -> new BLibEventHandle<>(mod) {

        private final BLibTagsUpdatedEvent dispatcher = (registryAccess, fromClientPacket) -> CommonLifecycleEvents.TAGS_LOADED
            .invoker()
            .onTagsLoaded(registryAccess, fromClientPacket);

        @Override
        public BLibTagsUpdatedEvent dispatcher() {
            return dispatcher;
        }

        @Override
        public void onRegister(BLibTagsUpdatedEvent event) {
            CommonLifecycleEvents.TAGS_LOADED.register((event::invoke));
        }
    };
}
