package com.blib.api.common.property.v1.serializer;

import org.jetbrains.annotations.Nullable;

public interface BLibPropertySerializer<T> {

    @Nullable
    T deserializeOrNull(String raw);

    String serialize(T value);
}
