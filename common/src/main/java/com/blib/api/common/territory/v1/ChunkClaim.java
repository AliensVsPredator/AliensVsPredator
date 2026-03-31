package com.blib.api.common.territory.v1;

import net.minecraft.resources.ResourceLocation;

public record ChunkClaim(
    Claimant claimant,
    ResourceLocation reason,
    long claimedAtTick
) {}
