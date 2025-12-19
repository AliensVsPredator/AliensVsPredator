package com.blib.fabric.internal.event.impl;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import org.jetbrains.annotations.ApiStatus;

import com.blib.common.event.BLibBlockBreakEvent;
import com.blib.common.event.BLibEventRouter;

@ApiStatus.Internal
public final class BLibFabricPlayerBlockBreakEvents {

    public static final BLibEventRouter<BLibBlockBreakEvent> BEFORE = new BLibEventRouter<>() {

        private final BLibBlockBreakEvent dispatcher = (level, player, blockPos, blockState) -> PlayerBlockBreakEvents.BEFORE
            .invoker()
            .beforeBlockBreak(level, player, blockPos, blockState, null);

        @Override
        public BLibBlockBreakEvent dispatcher() {
            return dispatcher;
        }

        @Override
        public void register(BLibBlockBreakEvent blockBreakEvent) {
            PlayerBlockBreakEvents.BEFORE.register(
                (level, player, pos, state, blockEntity) -> blockBreakEvent.invoke(level, player, pos, state)
            );
        }
    };
}
