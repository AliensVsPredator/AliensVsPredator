package com.avp.core.common.lifecycle.growth;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Predicate;
import java.util.function.Supplier;

public record GrowthStage(
    Supplier<? extends EntityType<? extends LivingEntity>> from,
    Supplier<? extends EntityType<? extends LivingEntity>> to,
    int growthTimeInTicks,
    Predicate<? super Entity> canMaturePredicate
) {

    public GrowthStage(
        Supplier<? extends EntityType<? extends LivingEntity>> from,
        Supplier<? extends EntityType<? extends LivingEntity>> to,
        int growthTimeInTicks
    ) {
        this(from, to, growthTimeInTicks, $ -> true);
    }
}
