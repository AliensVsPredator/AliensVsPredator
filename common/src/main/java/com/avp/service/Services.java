package com.avp.service;

import java.util.ServiceLoader;

import com.avp.AVP;

public class Services {

    public static final PlatformService PLATFORM = load(PlatformService.class);

    public static <T> T load(Class<T> clazz) {
        var loadedService = ServiceLoader.load(clazz)
            .findFirst()
            .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));

        AVP.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);

        return loadedService;
    }
}
