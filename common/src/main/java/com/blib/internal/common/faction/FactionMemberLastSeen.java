package com.blib.internal.common.faction;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.UUID;

public record FactionMemberLastSeen(
    UUID uuid,
    ResourceKey<Level> dimension,
    int chunkX,
    int chunkZ,
    long tick
) {}
