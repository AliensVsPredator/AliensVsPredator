package com.avp.fabric.goap;

public record TypedIdentifier<T>(String identifier) {

    @Override
    public String toString() {
        return identifier;
    }
}
