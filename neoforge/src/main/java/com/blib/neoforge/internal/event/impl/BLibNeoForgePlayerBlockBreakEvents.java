package com.blib.neoforge.internal.event.impl;

import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

import com.blib.BLibMod;
import com.blib.common.event.BLibBlockBreakEvent;
import com.blib.neoforge.event.BLibNeoForgeEventHandle;

@ApiStatus.Internal
public final class BLibNeoForgePlayerBlockBreakEvents {

    public static final Function<BLibMod, BLibNeoForgeEventHandle<BLibBlockBreakEvent>> FACTORY = mod -> new BLibNeoForgeEventHandle<>(
        mod
    ) {

        private final BLibBlockBreakEvent dispatcher = (level, player, blockPos, blockState) -> !NeoForge.EVENT_BUS.post(
            new BlockEvent.BreakEvent(level, blockPos, blockState, player)
        ).isCanceled();

        @Override
        public void initialize() {
            NeoForge.EVENT_BUS.<BlockEvent.BreakEvent>addListener(
                event -> listeners.forEach(
                    listener -> listener.invoke((Level) event.getLevel(), event.getPlayer(), event.getPos(), event.getState())
                )
            );
        }

        @Override
        public BLibBlockBreakEvent dispatcher() {
            return dispatcher;
        }
    };
}
