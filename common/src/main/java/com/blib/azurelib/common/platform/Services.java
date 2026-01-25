package com.blib.azurelib.common.platform;

import java.util.ServiceLoader;

import com.blib.azurelib.common.platform.services.*;

public final class Services {

    public static final AzureLibInitializer INITIALIZER = load(AzureLibInitializer.class);

    private Services() {
        throw new UnsupportedOperationException();
    }

    public static <T> T load(Class<T> clazz) {
        return ServiceLoader.load(clazz)
            .findFirst()
            .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
    }
}
