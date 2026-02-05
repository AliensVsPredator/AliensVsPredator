package com.blib.api.common.reputation.v1;

import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public sealed interface ReputationSubject permits ReputationSubject.Faction, ReputationSubject.Entity {

    record Faction(ResourceLocation factionId) implements ReputationSubject {}

    record Entity(UUID uuid) implements ReputationSubject {}

    static ReputationSubject faction(ResourceLocation id) {
        return new Faction(id);
    }

    static ReputationSubject entity(UUID uuid) {
        return new Entity(uuid);
    }

    static ReputationSubject entity(net.minecraft.world.entity.Entity entity) {
        return new Entity(entity.getUUID());
    }
}
