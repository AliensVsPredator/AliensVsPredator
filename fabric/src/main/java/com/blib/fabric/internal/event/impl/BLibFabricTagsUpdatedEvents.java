package com.blib.fabric.internal.event.impl;

import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

import com.blib.api.common.event.v1.BLibTagsUpdatedEvent;
import com.blib.api.common.event.v1.handle.BLibEventHandle;
import com.blib.api.common.mod.v1.BLibMod;

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
