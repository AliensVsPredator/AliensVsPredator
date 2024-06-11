package org.avp.api.util;

public class TypeUtil {

    @SuppressWarnings("unchecked")
    public static <T> T self(Object object) {
        return (T) object;
    }

    private TypeUtil() {
        throw new UnsupportedOperationException();
    }
}
