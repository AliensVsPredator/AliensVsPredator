package com.avp.service;

import com.blib.BLib;

import java.util.ServiceLoader;

@Deprecated(forRemoval = true)
public class Services {

    public static final RegistryService REGISTRY = load(RegistryService.class);

    public static <T> T load(Class<T> clazz) {
        var loadedService = ServiceLoader.load(clazz)
            .findFirst()
            .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));

        BLib.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);

        return loadedService;
    }
}
