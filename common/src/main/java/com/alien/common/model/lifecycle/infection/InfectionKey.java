package com.alien.common.model.lifecycle.infection;

import net.minecraft.world.entity.EntityType;

public record InfectionKey(
    EntityType<?> host,
    EntityType<?> parasite
) {}
