package com.blib.neoforge.event.impl;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import com.blib.common.event.BLibLevelTickEvent;
import com.blib.neoforge.event.NeoForgeBLibEventRouter;

public class NeoForgeBLibLevelTickEvents {

    public static final NeoForgeBLibEventRouter<BLibLevelTickEvent> AFTER = new NeoForgeBLibEventRouter<>() {

        // TODO: See if we can pass something other than () -> true for this event in the future.
        private final BLibLevelTickEvent dispatcher = (level) -> NeoForge.EVENT_BUS.post(
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
        public BLibLevelTickEvent dispatcher() {
            return dispatcher;
        }
    };

    public static final NeoForgeBLibEventRouter<BLibLevelTickEvent> BEFORE = new NeoForgeBLibEventRouter<>() {

        // TODO: See if we can pass something other than () -> true for this event in the future.
        private final BLibLevelTickEvent dispatcher = (level) -> NeoForge.EVENT_BUS.post(
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
        public BLibLevelTickEvent dispatcher() {
            return dispatcher;
        }
    };
}
