package com.blib.api.common.faction.v1;

import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public sealed interface FactionMember permits FactionMember.Entity, FactionMember.SubFaction {

    boolean matches(net.minecraft.world.entity.Entity entity);

    boolean matches(ReadableFactionRelationships faction);

    static FactionMember entity(UUID uuid) {
        return new Entity(uuid);
    }

    static FactionMember entity(net.minecraft.world.entity.Entity entity) {
        return new Entity(entity.getUUID());
    }

    static FactionMember subFaction(ResourceLocation factionId) {
        return new SubFaction(factionId);
    }

    record Entity(UUID uuid) implements FactionMember {

        @Override
        public boolean matches(net.minecraft.world.entity.Entity entity) {
            return entity.getUUID().equals(uuid);
        }

        @Override
        public boolean matches(ReadableFactionRelationships faction) {
            return false;
        }
    }

    record SubFaction(ResourceLocation factionId) implements FactionMember {

        @Override
        public boolean matches(net.minecraft.world.entity.Entity entity) {
            return false;
        }

        @Override
        public boolean matches(ReadableFactionRelationships faction) {
            return faction.getId().equals(factionId);
        }
    }
}
