package org.avp.common.data.alien.maturation;

import net.minecraft.world.entity.EntityType;

import java.util.List;
import java.util.Set;

public record AlienMaturation(
    Set<EntityType<?>> hosts,
    List<AlienMaturationStep> steps
) {}
