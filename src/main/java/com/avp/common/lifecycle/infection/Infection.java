package com.avp.common.lifecycle.infection;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public record Infection(
    EntityType<?> parasiteSourceType,
    Set<EntityType<?>> hosts,
    EntityType<? extends LivingEntity> parasiteType,
    int impregnationDelay,
    int detachDelay,
    int gestationTime
) {}
