package com.blib.fabric.service.impl;

import com.blib.event.key.BLibEventKey;
import com.blib.event.key.BLibEventKeys;
import com.blib.event.BLibLevelTickEvent;
import com.blib.service.BLibEventService;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.Map;
import java.util.function.Consumer;

public class FabricBLibEventServiceImpl implements BLibEventService {

    private final Map<BLibEventKey<?>, Consumer<Consumer<?>>> eventKeyToConsumerMap;

    public FabricBLibEventServiceImpl() {
        this.eventKeyToConsumerMap = Map.ofEntries(
            Map.entry(BLibEventKeys.LEVEL_TICK_PRE, FabricBLibEventServiceImpl::registerPreLevelTickEventCallback),
            Map.entry(BLibEventKeys.LEVEL_TICK_POST, FabricBLibEventServiceImpl::registerPostLevelTickEventCallback)
        );
    }

    @Override
    public <T> void addListener(BLibEventKey<T> key, Consumer<T> consumer) {
        eventKeyToConsumerMap.get(key).accept(consumer);
    }

    @SuppressWarnings("unchecked")
    private static void registerPreLevelTickEventCallback(Consumer<?> consumer) {
        ServerTickEvents.START_WORLD_TICK.register(
            level -> ((Consumer<BLibLevelTickEvent.Pre>) consumer).accept(new BLibLevelTickEvent.Pre(level))
        );
    }

    @SuppressWarnings("unchecked")
    private static void registerPostLevelTickEventCallback(Consumer<?> consumer) {
        ServerTickEvents.END_WORLD_TICK.register(
            level -> ((Consumer<BLibLevelTickEvent.Post>) consumer).accept(new BLibLevelTickEvent.Post(level))
        );
    }
}
