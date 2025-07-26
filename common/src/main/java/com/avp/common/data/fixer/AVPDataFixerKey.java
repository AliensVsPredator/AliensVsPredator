package com.avp.common.data.fixer;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public record AVPDataFixerKey(
    Registry<?> registry,
    ResourceLocation from
) {}
