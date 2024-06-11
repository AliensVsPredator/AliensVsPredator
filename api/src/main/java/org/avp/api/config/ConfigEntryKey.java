package org.avp.api.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ConfigEntryKey(
    @NotNull String name,
    @Nullable String category
) {}
