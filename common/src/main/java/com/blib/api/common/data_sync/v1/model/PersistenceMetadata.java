package com.blib.api.common.data_sync.v1.model;

import com.mojang.serialization.Codec;

public record PersistenceMetadata<T>(
    String key,
    Codec<T> codec
) {}
