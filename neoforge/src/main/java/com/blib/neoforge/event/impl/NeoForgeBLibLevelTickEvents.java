package com.blib.neoforge.event.impl;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import com.blib.internal.service.BLibEventService;
import com.blib.neoforge.event.NeoForgeBLibEventRouter;

public class NeoForgeBLibLevelTickEvents {

    public static final NeoForgeBLibEventRouter<BLibEventService.LevelTickEvent> AFTER = new NeoForgeBLibEventRouter<>() {

        // TODO: See if we can pass something other than () -> true for this event in the future.
        private final BLibEventService.LevelTickEvent dispatcher = (level) -> NeoForge.EVENT_BUS.post(
            new LevelTickEvent.Post(() -> true, level)
        );

        @Override
        public void initialize() {
            NeoForge.EVENT_BUS.<LevelTickEvent.Post>addListener(
                event -> listeners.forEach(
                    listener -> listener.invoke(event.getLevel())
                )
            );
        }

        @Override
        public BLibEventService.LevelTickEvent dispatcher() {
            return dispatcher;
        }
    };

    public static final NeoForgeBLibEventRouter<BLibEventService.LevelTickEvent> BEFORE = new NeoForgeBLibEventRouter<>() {

        // TODO: See if we can pass something other than () -> true for this event in the future.
        private final BLibEventService.LevelTickEvent dispatcher = (level) -> NeoForge.EVENT_BUS.post(
            new LevelTickEvent.Pre(() -> true, level)
        );

        @Override
        public void initialize() {
            NeoForge.EVENT_BUS.<LevelTickEvent.Pre>addListener(
                event -> listeners.forEach(
                    listener -> listener.invoke(event.getLevel())
                )
            );
        }

        @Override
        public BLibEventService.LevelTickEvent dispatcher() {
            return dispatcher;
        }
    };
}
