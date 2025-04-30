package com.avp.fabric.common.lifecycle;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Set;

import com.avp.fabric.common.lifecycle.growth.GrowthStage;

public record AlienLifecycle(
    Set<EntityType<? extends LivingEntity>> hosts,
    List<GrowthStage> stages
) {}
