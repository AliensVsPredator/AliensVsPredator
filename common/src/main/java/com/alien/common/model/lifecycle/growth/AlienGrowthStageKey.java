package com.alien.common.model.lifecycle.growth;

import net.minecraft.world.entity.EntityType;

public record AlienGrowthStageKey(
    EntityType<?> host,
    EntityType<?> other
) {}
