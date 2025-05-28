package com.xlib.goap.state;

import com.xlib.goap.TypedIdentifier;
import com.xlib.goap.effect.GOAPEffectContainer;

import java.util.HashMap;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

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

    public <T> void set(TypedIdentifier<T> key, UnaryOperator<T> unaryOperator, Supplier<T> supplyIfAbsent) {
        var previousValue = get(key);
        var mutatedValue = unaryOperator.apply(previousValue == null ? supplyIfAbsent.get() : previousValue);

        set(key, mutatedValue);
    }

    public void apply(GOAPEffectContainer effectContainer) {
        effectContainer.getEffects().forEach(effect -> effect.apply(this));
    }
}
