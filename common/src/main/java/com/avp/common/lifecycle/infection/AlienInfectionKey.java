package com.avp.common.lifecycle.infection;

import net.minecraft.world.entity.EntityType;

public record AlienInfectionKey(
    EntityType<?> host,
    EntityType<?> parasite
) {}
