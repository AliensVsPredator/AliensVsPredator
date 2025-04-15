package com.avp.common.lifecycle.infection;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public record Infection<S extends LivingEntity, P extends LivingEntity>(
    EntityType<S> parasiteSourceType,
    Set<EntityType<?>> hosts,
    EntityType<P> parasiteType,
    int impregnationDelay,
    int detachDelay,
    int gestationTime
) {}
