package com.blib.fabric.event;

import com.blib.event.BLibEventRouter;
import com.blib.service.BLibEventService;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;

public class FabricBLibPlayerBlockBreakEvents {

    public static final BLibEventRouter<BLibEventService.BlockBreakEvent> BEFORE = new BLibEventRouter<>() {

        private final BLibEventService.BlockBreakEvent dispatcher = (level, player, blockPos, blockState) ->
            PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(level, player, blockPos, blockState, null);

        @Override
        public BLibEventService.BlockBreakEvent dispatcher() {
            return dispatcher;
        }

        @Override
        public void register(BLibEventService.BlockBreakEvent blockBreakEvent) {
            PlayerBlockBreakEvents.BEFORE.register(
                (level, player, pos, state, blockEntity) ->
                    blockBreakEvent.invoke(level, player, pos, state)
            );
        }
    };
}
