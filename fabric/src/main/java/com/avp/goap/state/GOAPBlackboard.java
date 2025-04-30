package com.avp.goap.state;

import java.util.HashMap;

import com.avp.goap.TypedIdentifier;

public class GOAPBlackboard extends GOAPStateCache {

    public GOAPBlackboard() {
        super(new HashMap<>());
    }

    public <T> void set(TypedIdentifier<T> key, T value) {
        stateMap.put(key, value);
    }
}
