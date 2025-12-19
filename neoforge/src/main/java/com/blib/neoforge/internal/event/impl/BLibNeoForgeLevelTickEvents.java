package com.blib.neoforge.internal.event.impl;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.jetbrains.annotations.ApiStatus;

import com.blib.common.event.BLibLevelTickEvent;
import com.blib.neoforge.event.BLibNeoForgeEventRouter;

@ApiStatus.Internal
public final class BLibNeoForgeLevelTickEvents {

    public static final BLibNeoForgeEventRouter<BLibLevelTickEvent> AFTER = new BLibNeoForgeEventRouter<>() {

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

    public static final BLibNeoForgeEventRouter<BLibLevelTickEvent> BEFORE = new BLibNeoForgeEventRouter<>() {

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
