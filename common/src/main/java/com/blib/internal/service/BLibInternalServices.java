package com.blib.internal.service;

import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

@ApiStatus.Internal
public class BLibInternalServices {

    private static final Logger LOGGER = LoggerFactory.getLogger(BLibInternalServices.class);

    public static final BLibEventService EVENT = load(BLibEventService.class);

    public static final BLibFactoryService FACTORY = load(BLibFactoryService.class);

    public static final BLibModService MOD = load(BLibModService.class);

    public static final BLibModLoaderService MOD_LOADER = load(BLibModLoaderService.class);

    public static final BLibRegistryService REGISTRY = load(BLibRegistryService.class);

    public static final BLibServerNetworkingService SERVER_NETWORKING = load(BLibServerNetworkingService.class);

    private static <T> T load(Class<T> clazz) {
        var loadedService = ServiceLoader.load(clazz)
            .findFirst()
            .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));

        LOGGER.debug("Loaded {} for service {}", loadedService, clazz);

        return loadedService;
    }
}
