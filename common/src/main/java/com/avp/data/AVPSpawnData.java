package com.avp.data;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;

import com.avp.common.config.AVPConfig;

public record AVPSpawnData(
    EntityType<?> entityType,
    ResourceKey<?> spawnKey,
    TagKey<Biome> biomeTag,
    AVPConfig.SpawnConfigs.SpawnSettings config
) {}
