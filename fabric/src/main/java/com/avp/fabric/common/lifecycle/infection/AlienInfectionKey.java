package com.avp.fabric.common.lifecycle.infection;

import net.minecraft.world.entity.EntityType;

public record AlienInfectionKey(
    EntityType<?> host,
    EntityType<?> parasite
) {}
