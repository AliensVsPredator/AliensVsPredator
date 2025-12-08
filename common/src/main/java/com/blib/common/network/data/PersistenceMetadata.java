package com.blib.common.network.data;

import com.mojang.serialization.Codec;

public record PersistenceMetadata<T>(
    String key,
    Codec<T> codec
) {}
