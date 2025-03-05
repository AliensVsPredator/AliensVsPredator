package com.avp.core.common.lifecycle.infection;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;
import java.util.function.Supplier;

public record Infection(
    Supplier<? extends EntityType<?>> parasiteSourceType,
    Set<EntityType<?>> hosts,
    Supplier<? extends EntityType<? extends LivingEntity>> parasiteType,
    int impregnationDelay,
    int detachDelay,
    int gestationTime
) {}
