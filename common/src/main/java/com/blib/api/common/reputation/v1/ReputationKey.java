package com.blib.api.common.reputation.v1;

import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public sealed interface ReputationKey permits ReputationKey.Faction, ReputationKey.Entity {

    record Faction(ResourceLocation factionId) implements ReputationKey {}

    record Entity(UUID uuid) implements ReputationKey {}

    static ReputationKey faction(ResourceLocation id) {
        return new Faction(id);
    }

    static ReputationKey entity(UUID uuid) {
        return new Entity(uuid);
    }

    static ReputationKey entity(net.minecraft.world.entity.Entity entity) {
        return new Entity(entity.getUUID());
    }
}
