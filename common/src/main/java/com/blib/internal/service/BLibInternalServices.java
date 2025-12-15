package com.blib.internal.service;

import org.jetbrains.annotations.ApiStatus;

import java.util.ServiceLoader;

import com.blib.BLib;
import com.blib.service.BLibServices;

@ApiStatus.Internal
public class BLibInternalServices {

    public static final BLibEventService EVENT = BLibServices.load(BLibEventService.class);

    public static final BLibModService MOD = load(BLibModService.class);

    public static final BLibModLoaderService MOD_LOADER = load(BLibModLoaderService.class);

    public static final BLibRegistryService REGISTRY = load(BLibRegistryService.class);

    public static <T> T load(Class<T> clazz) {
        var loadedService = ServiceLoader.load(clazz)
            .findFirst()
            .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));

        BLib.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);

        return loadedService;
    }
}
