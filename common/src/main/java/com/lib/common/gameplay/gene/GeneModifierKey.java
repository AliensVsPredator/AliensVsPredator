package com.lib.common.gameplay.gene;

import net.minecraft.resources.ResourceLocation;

public record GeneModifierKey(
    ResourceLocation resourceLocation,
    GeneOperationType operation
) {}
