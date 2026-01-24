package com.blib.fabric.internal.event.impl;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

import com.blib.api.common.event.v1.BLibLevelTickEvent;
import com.blib.api.common.event.v1.handle.BLibEventHandle;
import com.blib.api.common.mod.v1.BLibMod;
import com.blib.api.common.mod.v1.model.DistributionEnvironmentType;
import com.blib.internal.service.BLibInternalServices;

@ApiStatus.Internal
public final class BLibFabricLevelTickEvents {

    public static final Function<BLibMod, BLibEventHandle<BLibLevelTickEvent>> POST_FACTORY = mod -> new BLibEventHandle<>(mod) {

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
        public void onRegister(BLibLevelTickEvent event) {
            if (BLibInternalServices.MOD_LOADER.getDistributionEnvironmentType() == DistributionEnvironmentType.CLIENT) {
                ClientTickEvents.END_WORLD_TICK.register(event::invoke);
            }

            ServerTickEvents.END_WORLD_TICK.register(event::invoke);
        }
    };

    public static final Function<BLibMod, BLibEventHandle<BLibLevelTickEvent>> PRE_FACTORY = mod -> new BLibEventHandle<>(mod) {

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
        public void onRegister(BLibLevelTickEvent event) {
            if (BLibInternalServices.MOD_LOADER.getDistributionEnvironmentType() == DistributionEnvironmentType.CLIENT) {
                ClientTickEvents.START_WORLD_TICK.register(event::invoke);
            }

            ServerTickEvents.START_WORLD_TICK.register(event::invoke);
        }
    };
}
