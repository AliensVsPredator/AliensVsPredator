package com.lib.common.network;

import com.mojang.serialization.Codec;

public record PersistenceMetadata<T>(
    String key,
    Codec<T> codec
) {}
