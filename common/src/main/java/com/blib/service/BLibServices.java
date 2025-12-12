package com.blib.service;

import com.blib.BLib;

import java.util.ServiceLoader;

public class BLibServices {

    public static final BLibClientNetworkingService CLIENT_NETWORKING = load(BLibClientNetworkingService.class);

    public static final BLibClientRegistryService CLIENT_REGISTRY = load(BLibClientRegistryService.class);

    public static final BLibEventService EVENT = load(BLibEventService.class);

    public static final BLibFactoryService FACTORY = load(BLibFactoryService.class);

    public static final BLibServerNetworkingService SERVER_NETWORKING = load(BLibServerNetworkingService.class);

    public static <T> T load(Class<T> clazz) {
        var loadedService = ServiceLoader.load(clazz)
            .findFirst()
            .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));

        BLib.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);

        return loadedService;
    }
}
