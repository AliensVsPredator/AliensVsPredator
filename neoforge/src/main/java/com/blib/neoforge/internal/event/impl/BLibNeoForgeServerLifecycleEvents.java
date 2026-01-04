package com.blib.neoforge.internal.event.impl;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

import com.blib.BLibMod;
import com.blib.common.event.BLibServerLifecycleEvent;
import com.blib.neoforge.event.BLibNeoForgeEventHandle;

@ApiStatus.Internal
public final class BLibNeoForgeServerLifecycleEvents {

    public static final Function<BLibMod, BLibNeoForgeEventHandle<BLibServerLifecycleEvent.Started>> STARTED_FACTORY =
        mod -> new BLibNeoForgeEventHandle<>(
            mod
        ) {

            private final BLibServerLifecycleEvent.Started dispatcher = (server) -> NeoForge.EVENT_BUS.post(
                new ServerStartedEvent(server)
            );

            @Override
            public void initialize() {
                NeoForge.EVENT_BUS.<ServerStartedEvent>addListener(
                    event -> listeners.forEach(
                        listener -> listener.invoke(event.getServer())
                    )
                );
            }

            @Override
            public BLibServerLifecycleEvent.Started dispatcher() {
                return dispatcher;
            }
        };

    public static final Function<BLibMod, BLibNeoForgeEventHandle<BLibServerLifecycleEvent.Starting>> STARTING_FACTORY =
        mod -> new BLibNeoForgeEventHandle<>(
            mod
        ) {

            private final BLibServerLifecycleEvent.Starting dispatcher = (server) -> NeoForge.EVENT_BUS.post(
                new ServerStartingEvent(server)
            );

            @Override
            public void initialize() {
                NeoForge.EVENT_BUS.<ServerStartingEvent>addListener(
                    event -> listeners.forEach(
                        listener -> listener.invoke(event.getServer())
                    )
                );
            }

            @Override
            public BLibServerLifecycleEvent.Starting dispatcher() {
                return dispatcher;
            }
        };

    public static final Function<BLibMod, BLibNeoForgeEventHandle<BLibServerLifecycleEvent.Stopped>> STOPPED_FACTORY =
        mod -> new BLibNeoForgeEventHandle<>(
            mod
        ) {

            private final BLibServerLifecycleEvent.Stopped dispatcher = (server) -> NeoForge.EVENT_BUS.post(
                new ServerStoppedEvent(server)
            );

            @Override
            public void initialize() {
                NeoForge.EVENT_BUS.<ServerStoppedEvent>addListener(
                    event -> listeners.forEach(
                        listener -> listener.invoke(event.getServer())
                    )
                );
            }

            @Override
            public BLibServerLifecycleEvent.Stopped dispatcher() {
                return dispatcher;
            }
        };

    public static final Function<BLibMod, BLibNeoForgeEventHandle<BLibServerLifecycleEvent.Stopping>> STOPPING_FACTORY =
        mod -> new BLibNeoForgeEventHandle<>(
            mod
        ) {

            private final BLibServerLifecycleEvent.Stopping dispatcher = (server) -> NeoForge.EVENT_BUS.post(
                new ServerStoppingEvent(server)
            );

            @Override
            public void initialize() {
                NeoForge.EVENT_BUS.<ServerStoppingEvent>addListener(
                    event -> listeners.forEach(
                        listener -> listener.invoke(event.getServer())
                    )
                );
            }

            @Override
            public BLibServerLifecycleEvent.Stopping dispatcher() {
                return dispatcher;
            }
        };
}
