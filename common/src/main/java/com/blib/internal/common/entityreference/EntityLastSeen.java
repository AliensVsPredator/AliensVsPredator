package com.blib.internal.common.entityreference;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.UUID;

public record EntityLastSeen(
    UUID uuid,
    ResourceKey<Level> dimension,
    int chunkX,
    int chunkZ,
    long tick
) {}
