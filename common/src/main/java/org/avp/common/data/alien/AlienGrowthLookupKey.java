package org.avp.common.data.alien;

import net.minecraft.world.entity.EntityType;

public record AlienGrowthLookupKey(
    EntityType<?> host,
    EntityType<?> other
) {}
