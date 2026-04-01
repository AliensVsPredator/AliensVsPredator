package com.blib.api.common.faction.v1;

import net.minecraft.resources.ResourceLocation;

public record FactionPair(
    ResourceLocation first,
    ResourceLocation second
) {

    public FactionPair {
        if (first.compareTo(second) > 0) {
            var temp = first;
            first = second;
            second = temp;
        }
    }

    public static FactionPair of(ResourceLocation a, ResourceLocation b) {
        return new FactionPair(a, b);
    }

    public boolean contains(ResourceLocation factionId) {
        return first.equals(factionId) || second.equals(factionId);
    }

    public ResourceLocation other(ResourceLocation factionId) {
        if (first.equals(factionId)) {
            return second;
        }

        if (second.equals(factionId)) {
            return first;
        }

        throw new IllegalArgumentException("Faction '%s' is not part of this pair".formatted(factionId));
    }
}
