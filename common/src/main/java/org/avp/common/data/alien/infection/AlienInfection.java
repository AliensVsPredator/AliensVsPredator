package org.avp.common.data.alien.infection;

import net.minecraft.world.entity.EntityType;

import java.util.Set;

public record AlienInfection(
    EntityType<?> parasite,
    Set<EntityType<?>> hosts,
    EntityType<?> result,
    int impregnationDelay,
    int detachDelay,
    int gestationTime
) {}
