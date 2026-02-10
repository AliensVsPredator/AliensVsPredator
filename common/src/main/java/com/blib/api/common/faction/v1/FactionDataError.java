package com.blib.api.common.faction.v1;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public sealed interface FactionDataError {

    @Nullable
    FactionData data();

    record UnknownType(
        ResourceLocation factionId,
        @Nullable FactionData data
    ) implements FactionDataError {}

    record TypeMismatch(
        ResourceLocation expected,
        ResourceLocation actual,
        @Nullable FactionData data
    ) implements FactionDataError {}
}
