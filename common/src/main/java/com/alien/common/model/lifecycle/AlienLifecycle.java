package com.alien.common.model.lifecycle;

import com.alien.common.model.lifecycle.growth.GrowthStage;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Set;

public record AlienLifecycle(
    Set<EntityType<? extends LivingEntity>> hosts,
    List<GrowthStage> stages
) {}
