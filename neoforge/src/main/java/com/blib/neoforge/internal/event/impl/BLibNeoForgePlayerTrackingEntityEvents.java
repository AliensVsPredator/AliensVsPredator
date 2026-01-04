package com.blib.neoforge.internal.event.impl;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

import com.blib.BLibMod;
import com.blib.common.event.BLibPlayerTrackingEntityEvent;
import com.blib.neoforge.event.BLibNeoForgeEventHandle;

@ApiStatus.Internal
public final class BLibNeoForgePlayerTrackingEntityEvents {

    public static final Function<BLibMod, BLibNeoForgeEventHandle<BLibPlayerTrackingEntityEvent>> FACTORY =
        mod -> new BLibNeoForgeEventHandle<>(mod) {

            private final BLibPlayerTrackingEntityEvent dispatcher = (trackedEntity, player) -> NeoForge.EVENT_BUS.post(
                new PlayerEvent.StartTracking(player, trackedEntity)
            );

            @Override
            public void initialize() {
                NeoForge.EVENT_BUS.<PlayerEvent.StartTracking>addListener(
                    event -> listeners.forEach(
                        listener -> listener.invoke(event.getTarget(), event.getEntity())
                    )
                );
            }

            @Override
            public BLibPlayerTrackingEntityEvent dispatcher() {
                return dispatcher;
            }
        };
}
