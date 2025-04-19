package com.avp.goap;

public record TypedIdentifier<T>(String identifier) {

    @Override
    public String toString() {
        return identifier;
    }
}
