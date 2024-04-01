package org.avp.common.util;

public class MixinUtilities {

    @SuppressWarnings("unchecked")
    public static <T> T self(Object object) {
        return (T) object;
    }

    private MixinUtilities() {
        throw new UnsupportedOperationException();
    }
}
