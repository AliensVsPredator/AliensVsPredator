package com.avp.common.lifecycle.growth;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Predicate;

public record GrowthStage(
    EntityType<? extends LivingEntity> from,
    EntityType<? extends LivingEntity> to,
    int growthTimeInTicks,
    Predicate<? super Entity> canMaturePredicate
) {

    public GrowthStage(
        EntityType<? extends LivingEntity> from,
        EntityType<? extends LivingEntity> to,
        int growthTimeInTicks
    ) {
        this(from, to, growthTimeInTicks, $ -> true);
    }
}
