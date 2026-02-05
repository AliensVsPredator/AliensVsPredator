package com.blib.api.common.faction.v1;

import java.util.function.Supplier;

public record FactionType<T extends FactionData>(Supplier<T> factory) {

    public T createInstance() {
        return factory.get();
    }
}
