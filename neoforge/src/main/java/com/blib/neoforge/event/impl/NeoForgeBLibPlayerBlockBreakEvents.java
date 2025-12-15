package com.blib.neoforge.event.impl;

import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;

import com.blib.common.event.BLibBlockBreakEvent;
import com.blib.neoforge.event.NeoForgeBLibEventRouter;

public class NeoForgeBLibPlayerBlockBreakEvents {

    public static final NeoForgeBLibEventRouter<BLibBlockBreakEvent> BEFORE = new NeoForgeBLibEventRouter<>() {

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
