package com.blib.api.common.faction.v1;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public record SubFactionMember(ResourceLocation factionId) implements FactionMember {

    @Override
    public boolean matches(Entity entity) {
        return false;
    }

    @Override
    public boolean matches(ReadableFaction faction) {
        return faction.getId().equals(factionId);
    }
}
