package com.blib.service;

import java.util.ServiceLoader;

import com.blib.BLib;

public class BLibServices {

    public static final BLibClientRegistryService CLIENT_REGISTRY = load(BLibClientRegistryService.class);

    public static <T> T load(Class<T> clazz) {
        var loadedService = ServiceLoader.load(clazz)
            .findFirst()
            .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));

        BLib.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);

        return loadedService;
    }
}
