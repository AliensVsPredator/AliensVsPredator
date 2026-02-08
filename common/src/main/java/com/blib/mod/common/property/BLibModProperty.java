package com.blib.mod.common.property;

import com.blib.api.common.property.v1.BLibPropertyKey;

public record BLibModProperty<T>(
    BLibPropertyKey.Leaf<T> key,
    T defaultValue
) {}
