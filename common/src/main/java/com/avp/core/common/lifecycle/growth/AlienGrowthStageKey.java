package com.avp.core.common.lifecycle.growth;

import net.minecraft.world.entity.EntityType;

public record AlienGrowthStageKey(
    EntityType<?> host,
    EntityType<?> other
) {}
