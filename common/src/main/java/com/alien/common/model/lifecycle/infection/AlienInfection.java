package com.alien.common.model.lifecycle.infection;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public record AlienInfection<S extends LivingEntity, P extends LivingEntity>(
    EntityType<S> parasiteType,
    Set<EntityType<?>> hosts,
    EntityType<P> embryoType,
    int impregnationDelay,
    int detachDelay,
    int gestationTime
) {}
