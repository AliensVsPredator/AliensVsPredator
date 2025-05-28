package com.xlib.goap.state;

import com.xlib.goap.TypedIdentifier;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class GOAPStateCache {

    protected final Map<TypedIdentifier<?>, Object> stateMap;

    protected GOAPStateCache(Map<TypedIdentifier<?>, Object> stateMap) {
        this.stateMap = stateMap;
    }

    @SuppressWarnings("unchecked")
    public <T> @Nullable T get(TypedIdentifier<T> key) {
        return (T) stateMap.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(TypedIdentifier<T> key, T defaultValue) {
        return (T) stateMap.getOrDefault(key, defaultValue);
    }
}
