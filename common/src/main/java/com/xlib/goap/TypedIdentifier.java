package com.xlib.goap;

public record TypedIdentifier<T>(String identifier) {

    @Override
    public String toString() {
        return identifier;
    }
}
