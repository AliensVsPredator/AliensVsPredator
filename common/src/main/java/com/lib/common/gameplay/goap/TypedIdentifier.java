package com.lib.common.gameplay.goap;

public record TypedIdentifier<T>(String identifier) {

    @Override
    public String toString() {
        return identifier;
    }
}
