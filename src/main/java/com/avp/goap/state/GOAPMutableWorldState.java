package com.avp.goap.state;

import java.util.HashMap;

import com.avp.goap.TypedIdentifier;

public class GOAPMutableWorldState extends GOAPWorldState {

    public GOAPMutableWorldState() {
        this(new HashMap<>());
    }

    public GOAPMutableWorldState(GOAPWorldState worldState) {
        this(new HashMap<>(worldState.stateMap));
    }

    public GOAPMutableWorldState(HashMap<TypedIdentifier<?>, Object> stateMap) {
        super(stateMap);
    }

    public <T> void set(TypedIdentifier<T> key, T value) {
        stateMap.put(key, value);
    }

    public void apply(GOAPWorldState worldState) {
        stateMap.putAll(worldState.stateMap);
    }
}
