package com.blib.neoforge.service.impl;

import com.blib.BLibMod;
import com.blib.event.BLibTagsUpdatedEvent;
import com.blib.event.key.BLibEventKey;
import com.blib.event.key.BLibEventKeys;
import com.blib.event.BLibLevelTickEvent;
import com.blib.service.BLibEventService;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class NeoForgeBLibEventServiceImpl implements BLibEventService {

    private final Map<BLibEventKey<?>, List<Consumer<?>>> eventKeyToConsumerMap;

    public NeoForgeBLibEventServiceImpl() {
        this.eventKeyToConsumerMap = new HashMap<>();
    }

    public void finalize(BLibMod mod) {
        NeoForge.EVENT_BUS.<LevelTickEvent.Pre>addListener(event -> {
            var wrappedEvent = new BLibLevelTickEvent.Pre(event.getLevel());
            var consumers = getEventListeners(BLibEventKeys.LEVEL_TICK_PRE);
            consumers.forEach(consumer -> consumer.accept(wrappedEvent));
        });
        NeoForge.EVENT_BUS.<LevelTickEvent.Post>addListener(event -> {
            var wrappedEvent = new BLibLevelTickEvent.Post(event.getLevel());
            var consumers = getEventListeners(BLibEventKeys.LEVEL_TICK_POST);
            consumers.forEach(consumer -> consumer.accept(wrappedEvent));
        });
        NeoForge.EVENT_BUS.<TagsUpdatedEvent>addListener(event -> {
            var wrappedEvent = new BLibTagsUpdatedEvent(event.getRegistryAccess(), event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED);
            var consumers = getEventListeners(BLibEventKeys.TAGS_UPDATED);
            consumers.forEach(consumer -> consumer.accept(wrappedEvent));
        });
    }

    @Override
    public <T> void addListener(BLibEventKey<T> key, Consumer<T> consumer) {
        eventKeyToConsumerMap.computeIfAbsent(key, $ -> new ArrayList<>())
            .add(consumer);
    }

    @SuppressWarnings("unchecked")
    public <T> List<Consumer<T>> getEventListeners(BLibEventKey<T> key) {
        return (List<Consumer<T>>) (List<?>) eventKeyToConsumerMap.getOrDefault(key, List.of());
    }

}
