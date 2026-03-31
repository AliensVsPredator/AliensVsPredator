package com.blib.api.common.faction.v1;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public interface WritableFactionRelationships extends ReadableFactionRelationships {

    boolean addMember(FactionMember member);

    boolean removeMember(FactionMember member);

    default boolean addEntity(UUID uuid) {
        return addMember(FactionMember.entity(uuid));
    }

    boolean addEntity(Entity entity);

    default boolean addSubFaction(ResourceLocation factionId) {
        return addMember(FactionMember.subFaction(factionId));
    }

    default boolean removeEntity(UUID uuid) {
        return removeMember(FactionMember.entity(uuid));
    }

    default boolean removeEntity(Entity entity) {
        return removeMember(FactionMember.entity(entity));
    }

    default boolean removeSubFaction(ResourceLocation factionId) {
        return removeMember(FactionMember.subFaction(factionId));
    }
}
