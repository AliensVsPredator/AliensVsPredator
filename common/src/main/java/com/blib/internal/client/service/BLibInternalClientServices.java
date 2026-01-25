package com.blib.internal.client.service;

import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

@ApiStatus.Internal
public class BLibInternalClientServices {

    private static final Logger LOGGER = LoggerFactory.getLogger(BLibInternalClientServices.class);

    public static final BLibClientEventService CLIENT_EVENT = load(BLibClientEventService.class);

    public static final BLibClientModService CLIENT_MOD = load(BLibClientModService.class);

    public static final BLibClientNetworkingService CLIENT_NETWORKING = load(BLibClientNetworkingService.class);

    public static final BLibClientRegistryService CLIENT_REGISTRY = load(BLibClientRegistryService.class);

    private static <T> T load(Class<T> clazz) {
        var loadedService = ServiceLoader.load(clazz)
            .findFirst()
            .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));

        LOGGER.debug("Loaded {} for service {}", loadedService, clazz);

        return loadedService;
    }
}
