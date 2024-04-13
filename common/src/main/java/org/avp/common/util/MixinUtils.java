package org.avp.common.util;

public class MixinUtils {

    @SuppressWarnings("unchecked")
    public static <T> T self(Object object) {
        return (T) object;
    }

    private MixinUtils() {
        throw new UnsupportedOperationException();
    }
}
