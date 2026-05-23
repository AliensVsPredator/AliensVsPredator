package com.blib.api.common.faction.v1;

import java.util.UUID;

public sealed interface FactionMember permits FactionMember.Entity {

    boolean matches(net.minecraft.world.entity.Entity entity);

    static FactionMember entity(UUID uuid) {
        return new Entity(uuid);
    }

    static FactionMember entity(net.minecraft.world.entity.Entity entity) {
        return new Entity(entity.getUUID());
    }

    record Entity(UUID uuid) implements FactionMember {

        @Override
        public boolean matches(net.minecraft.world.entity.Entity entity) {
            return entity.getUUID().equals(uuid);
        }
    }
}
