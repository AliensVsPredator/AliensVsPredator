package com.avp.core.common.config.property;

public record ConfigPropertyKey<T>(String identifier) {

    @SuppressWarnings("unchecked")
    public T cast(Object object) {
        return object == null ? null : (T) object;
    }
}
