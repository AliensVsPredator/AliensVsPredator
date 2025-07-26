package com.alien.common.model.lifecycle;

import com.alien.common.model.lifecycle.growth.GrowthStage;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import java.util.List;

public record AlienLifecycle(
    TagKey<EntityType<?>> hostKey,
    List<GrowthStage> stages
) {}
