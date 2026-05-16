package com.blib.api.common.event.v1;

import net.minecraft.resources.ResourceLocation;

import com.blib.api.common.faction.v1.RelationshipState;

@FunctionalInterface
public interface BLibFactionRelationshipChangedEvent {

    void invoke(ResourceLocation factionA, ResourceLocation factionB, RelationshipState oldState, RelationshipState newState);
}
