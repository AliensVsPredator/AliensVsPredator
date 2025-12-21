package com.blib.fabric.internal.event.impl;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

import com.blib.BLibMod;
import com.blib.common.event.BLibBlockBreakEvent;
import com.blib.common.event.BLibEventHandle;

@ApiStatus.Internal
public final class BLibFabricPlayerBlockBreakEvents {

    public static final Function<BLibMod, BLibEventHandle<BLibBlockBreakEvent>> FACTORY = mod -> new BLibEventHandle<>(mod) {

        private final BLibBlockBreakEvent dispatcher = (level, player, blockPos, blockState) -> PlayerBlockBreakEvents.BEFORE
            .invoker()
            .beforeBlockBreak(level, player, blockPos, blockState, null);

        @Override
        public BLibBlockBreakEvent dispatcher() {
            return dispatcher;
        }

        @Override
        public void onRegister(BLibBlockBreakEvent blockBreakEvent) {
            PlayerBlockBreakEvents.BEFORE.register(
                (level, player, pos, state, blockEntity) -> blockBreakEvent.invoke(level, player, pos, state)
            );
        }
    };
}
