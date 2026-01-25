package com.blib.neoforge.internal.event.impl;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

import com.blib.api.common.event.v1.BLibPlayerTrackingEntityEvent;
import com.blib.api.common.mod.v1.BLibMod;
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
