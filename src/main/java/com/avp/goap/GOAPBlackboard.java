package com.avp.goap;

public class GOAPBlackboard extends GOAPDataCache {
    public <T> void set(TypedIdentifier<T> key, T value) {
        stateMap.put(key, value);
    }
}
