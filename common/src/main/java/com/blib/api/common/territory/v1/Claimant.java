package com.blib.api.common.territory.v1;

import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public sealed interface Claimant permits Claimant.EntityClaimant, Claimant.FactionClaimant {

    static Claimant entity(UUID entityId) {
        return new EntityClaimant(entityId);
    }

    static Claimant faction(ResourceLocation factionId) {
        return new FactionClaimant(factionId);
    }

    record EntityClaimant(UUID entityId) implements Claimant {}

    record FactionClaimant(ResourceLocation factionId) implements Claimant {}
}
