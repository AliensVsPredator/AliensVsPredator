package com.avp.core.platform.service;

import java.util.ServiceLoader;

public class Services {

    public static final ClientRegistryService CLIENT_REGISTRY = load(ClientRegistryService.class);

    public static final NetworkService NETWORK = load(NetworkService.class);

    public static final PlatformService PLATFORM = load(PlatformService.class);

    public static final RegistryService REGISTRY = load(RegistryService.class);

    public static <T> T load(Class<T> clazz) {
        return ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
    }
}