package com.blib.fabric.internal.event.impl;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

import com.blib.api.common.event.v1.BLibBlockBreakEvent;
import com.blib.api.common.event.v1.handle.BLibEventHandle;
import com.blib.api.common.mod.v1.BLibMod;

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
        public void onRegister(BLibBlockBreakEvent event) {
            PlayerBlockBreakEvents.BEFORE.register(
                (level, player, pos, state, blockEntity) -> event.invoke(level, player, pos, state)
            );
        }
    };
}
