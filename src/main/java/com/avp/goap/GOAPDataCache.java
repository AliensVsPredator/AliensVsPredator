package com.avp.goap;

import java.util.HashMap;
import java.util.Map;

public abstract class GOAPDataCache {

    protected final Map<TypedIdentifier<?>, Object> stateMap;

    protected GOAPDataCache() {
        this(new HashMap<>());
    }

    public GOAPDataCache(Map<TypedIdentifier<?>, Object> stateMap) {
        this.stateMap = stateMap;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(TypedIdentifier<T> key) {
        return (T) stateMap.get(key);
    }
}
