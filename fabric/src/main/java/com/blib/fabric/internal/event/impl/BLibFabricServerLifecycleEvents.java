package com.blib.fabric.internal.event.impl;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

import com.blib.BLibMod;
import com.blib.common.event.BLibServerLifecycleEvent;
import com.blib.common.event.handle.BLibEventHandle;

@ApiStatus.Internal
public final class BLibFabricServerLifecycleEvents {

    public static final Function<BLibMod, BLibEventHandle<BLibServerLifecycleEvent.Started>> STARTED_FACTORY =
        mod -> new BLibEventHandle<>(mod) {

            private final BLibServerLifecycleEvent.Started dispatcher = (minecraftServer) -> ServerLifecycleEvents.SERVER_STARTED
                .invoker()
                .onServerStarted(minecraftServer);

            @Override
            public BLibServerLifecycleEvent.Started dispatcher() {
                return dispatcher;
            }

            @Override
            public void onRegister(BLibServerLifecycleEvent.Started event) {
                ServerLifecycleEvents.SERVER_STARTED.register(event::invoke);
            }
        };

    public static final Function<BLibMod, BLibEventHandle<BLibServerLifecycleEvent.Starting>> STARTING_FACTORY =
        mod -> new BLibEventHandle<>(mod) {

            private final BLibServerLifecycleEvent.Starting dispatcher = (minecraftServer) -> ServerLifecycleEvents.SERVER_STARTING
                .invoker()
                .onServerStarting(minecraftServer);

            @Override
            public BLibServerLifecycleEvent.Starting dispatcher() {
                return dispatcher;
            }

            @Override
            public void onRegister(BLibServerLifecycleEvent.Starting event) {
                ServerLifecycleEvents.SERVER_STARTING.register(event::invoke);
            }
        };

    public static final Function<BLibMod, BLibEventHandle<BLibServerLifecycleEvent.Stopped>> STOPPED_FACTORY =
        mod -> new BLibEventHandle<>(mod) {

            private final BLibServerLifecycleEvent.Stopped dispatcher = (minecraftServer) -> ServerLifecycleEvents.SERVER_STOPPED
                .invoker()
                .onServerStopped(minecraftServer);

            @Override
            public BLibServerLifecycleEvent.Stopped dispatcher() {
                return dispatcher;
            }

            @Override
            public void onRegister(BLibServerLifecycleEvent.Stopped event) {
                ServerLifecycleEvents.SERVER_STOPPED.register(event::invoke);
            }
        };

    public static final Function<BLibMod, BLibEventHandle<BLibServerLifecycleEvent.Stopping>> STOPPING_FACTORY =
        mod -> new BLibEventHandle<>(mod) {

            private final BLibServerLifecycleEvent.Stopping dispatcher = (minecraftServer) -> ServerLifecycleEvents.SERVER_STOPPING
                .invoker()
                .onServerStopping(minecraftServer);

            @Override
            public BLibServerLifecycleEvent.Stopping dispatcher() {
                return dispatcher;
            }

            @Override
            public void onRegister(BLibServerLifecycleEvent.Stopping event) {
                ServerLifecycleEvents.SERVER_STOPPING.register(event::invoke);
            }
        };
}
