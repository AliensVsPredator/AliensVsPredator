package com.blib.service;

import com.blib.BLib;

import java.util.ServiceLoader;

public class BLibServices {

    public static final BLibEventService EVENT = load(BLibEventService.class);

    public static final BLibFactoryService FACTORY = load(BLibFactoryService.class);

    public static final BLibModLoaderService MOD_LOADER = load(BLibModLoaderService.class);

    public static final BLibRegistryService REGISTRY = load(BLibRegistryService.class);

    private static <T> T load(Class<T> clazz) {
        var loadedService = ServiceLoader.load(clazz)
            .findFirst()
            .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));

        BLib.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);

        return loadedService;
    }
}
