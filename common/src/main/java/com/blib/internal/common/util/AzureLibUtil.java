package com.blib.internal.common.util;

public record AzureLibUtil() {

    public static <T> T self(Object object) {
        return (T) object;
    }
}
