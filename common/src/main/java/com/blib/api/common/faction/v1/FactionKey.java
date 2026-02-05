package com.blib.api.common.faction.v1;

import net.minecraft.resources.ResourceLocation;

import com.blib.api.common.registry.v1.BLibHolder;

public record FactionKey<T extends FactionData>(
    ResourceLocation id,
    BLibHolder<FactionType<T>> type
) {}
