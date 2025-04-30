package com.avp.fabric.goap.effect;

import java.util.function.UnaryOperator;

import com.avp.fabric.goap.TypedIdentifier;
import com.avp.fabric.goap.state.GOAPMutableWorldState;

public sealed interface GOAPEffect<T> {

    TypedIdentifier<T> identifier();

    void apply(GOAPMutableWorldState worldState);

    record Value<T>(
        TypedIdentifier<T> identifier,
        T value
    ) implements GOAPEffect<T> {

        @Override
        public void apply(GOAPMutableWorldState worldState) {
            worldState.set(identifier, value);
        }
    }

    record Dynamic<T>(
        TypedIdentifier<T> identifier,
        UnaryOperator<T> consumer
    ) implements GOAPEffect<T> {

        @Override
        public void apply(GOAPMutableWorldState worldState) {
            var existingValue = worldState.get(identifier);

            if (existingValue != null) {
                var updatedValue = consumer.apply(existingValue);
                worldState.set(identifier, updatedValue);
            }
        }
    }
}
