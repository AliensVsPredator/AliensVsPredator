package com.blib.fabric.event;

import com.blib.common.model.DistributionEnvironmentType;
import com.blib.event.BLibEventRouter;
import com.blib.service.BLibEventService;
import com.blib.service.BLibServices;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;

public class FabricBLibLevelTickEvents {

    public static final BLibEventRouter<BLibEventService.LevelTickEvent> AFTER = new BLibEventRouter<>() {

        private final BLibEventService.LevelTickEvent dispatcher = (level) -> {
            if (level.isClientSide) {
                ClientTickEvents.END_WORLD_TICK.invoker().onEndTick((ClientLevel) level);
            } else {
                ServerTickEvents.END_WORLD_TICK.invoker().onEndTick((ServerLevel) level);
            }
        };

        @Override
        public BLibEventService.LevelTickEvent dispatcher() {
            return dispatcher;
        }

        @Override
        public void register(BLibEventService.LevelTickEvent levelTickEvent) {
            if (BLibServices.MOD_LOADER.getDistributionEnvironmentType() == DistributionEnvironmentType.CLIENT) {
                ClientTickEvents.END_WORLD_TICK.register(levelTickEvent::invoke);
            }

            ServerTickEvents.END_WORLD_TICK.register(levelTickEvent::invoke);
        }
    };

    public static final BLibEventRouter<BLibEventService.LevelTickEvent> BEFORE = new BLibEventRouter<>() {

        private final BLibEventService.LevelTickEvent dispatcher = (level) -> {
            if (level.isClientSide) {
                ClientTickEvents.START_WORLD_TICK.invoker().onStartTick((ClientLevel) level);
            } else {
                ServerTickEvents.START_WORLD_TICK.invoker().onStartTick((ServerLevel) level);
            }
        };

        @Override
        public BLibEventService.LevelTickEvent dispatcher() {
            return dispatcher;
        }

        @Override
        public void register(BLibEventService.LevelTickEvent levelTickEvent) {
            if (BLibServices.MOD_LOADER.getDistributionEnvironmentType() == DistributionEnvironmentType.CLIENT) {
                ClientTickEvents.START_WORLD_TICK.register(levelTickEvent::invoke);
            }

            ServerTickEvents.START_WORLD_TICK.register(levelTickEvent::invoke);
        }
    };
}
