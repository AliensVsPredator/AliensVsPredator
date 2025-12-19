package com.blib.neoforge.internal.event.impl;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import org.jetbrains.annotations.ApiStatus;

import com.blib.common.event.BLibTagsUpdatedEvent;
import com.blib.neoforge.event.BLibNeoForgeEventRouter;

@ApiStatus.Internal
public final class BLibNeoForgeTagsUpdatedEvents {

    public static final BLibNeoForgeEventRouter<BLibTagsUpdatedEvent> ROUTER = new BLibNeoForgeEventRouter<>() {

        // TODO: See if we can determine integrated server connection here in the future.
        private final BLibTagsUpdatedEvent dispatcher = (registryAccess, fromClientPacket) -> NeoForge.EVENT_BUS.post(
            new TagsUpdatedEvent(registryAccess, fromClientPacket, false)
        );

        @Override
        public void initialize() {
            NeoForge.EVENT_BUS.<TagsUpdatedEvent>addListener(
                event -> listeners.forEach(
                    listener -> listener.invoke(
                        event.getRegistryAccess(),
                        event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED
                    )
                )
            );
        }

        @Override
        public BLibTagsUpdatedEvent dispatcher() {
            return dispatcher;
        }
    };
}
