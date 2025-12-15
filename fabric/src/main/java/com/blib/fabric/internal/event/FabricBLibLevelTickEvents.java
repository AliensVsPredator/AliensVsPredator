package com.blib.fabric.internal.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.ApiStatus;

import com.blib.common.event.BLibEventRouter;
import com.blib.common.event.BLibLevelTickEvent;
import com.blib.common.model.DistributionEnvironmentType;
import com.blib.internal.service.BLibInternalServices;

@ApiStatus.Internal
public final class FabricBLibLevelTickEvents {

    public static final BLibEventRouter<BLibLevelTickEvent> AFTER = new BLibEventRouter<>() {

        private final BLibLevelTickEvent dispatcher = (level) -> {
            if (level.isClientSide) {
                ClientTickEvents.END_WORLD_TICK.invoker().onEndTick((ClientLevel) level);
            } else {
                ServerTickEvents.END_WORLD_TICK.invoker().onEndTick((ServerLevel) level);
            }
        };

        @Override
        public BLibLevelTickEvent dispatcher() {
            return dispatcher;
        }

        @Override
        public void register(BLibLevelTickEvent levelTickEvent) {
            if (BLibInternalServices.MOD_LOADER.getDistributionEnvironmentType() == DistributionEnvironmentType.CLIENT) {
                ClientTickEvents.END_WORLD_TICK.register(levelTickEvent::invoke);
            }

            ServerTickEvents.END_WORLD_TICK.register(levelTickEvent::invoke);
        }
    };

    public static final BLibEventRouter<BLibLevelTickEvent> BEFORE = new BLibEventRouter<>() {

        private final BLibLevelTickEvent dispatcher = (level) -> {
            if (level.isClientSide) {
                ClientTickEvents.START_WORLD_TICK.invoker().onStartTick((ClientLevel) level);
            } else {
                ServerTickEvents.START_WORLD_TICK.invoker().onStartTick((ServerLevel) level);
            }
        };

        @Override
        public BLibLevelTickEvent dispatcher() {
            return dispatcher;
        }

        @Override
        public void register(BLibLevelTickEvent levelTickEvent) {
            if (BLibInternalServices.MOD_LOADER.getDistributionEnvironmentType() == DistributionEnvironmentType.CLIENT) {
                ClientTickEvents.START_WORLD_TICK.register(levelTickEvent::invoke);
            }

            ServerTickEvents.START_WORLD_TICK.register(levelTickEvent::invoke);
        }
    };
}
