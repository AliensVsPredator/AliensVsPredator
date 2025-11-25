package com.blib.service;

import java.util.ServiceLoader;

import com.avp.AVP;

public class BLibServices {

    public static final BLibEventService EVENT = load(BLibEventService.class);

    public static final BLibRegistryService REGISTRY = load(BLibRegistryService.class);

    public static <T> T load(Class<T> clazz) {
        var loadedService = ServiceLoader.load(clazz)
            .findFirst()
            .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));

        AVP.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);

        return loadedService;
    }
}
