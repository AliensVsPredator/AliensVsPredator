package com.alien.common.model.lifecycle.growth;

import net.minecraft.world.entity.EntityType;

public record GrowthStageKey(
    EntityType<?> host,
    EntityType<?> other
) {}
