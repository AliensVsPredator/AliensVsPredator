package com.avp.service;

import java.util.ServiceLoader;

import com.avp.AVP;

@Deprecated(forRemoval = true)
public class Services {

    public static final BridgeService BRIDGE = load(BridgeService.class);

    public static final ClientNetworkingService CLIENT_NETWORKING = load(ClientNetworkingService.class);

    public static final ClientRegistryService CLIENT_REGISTRY = load(ClientRegistryService.class);

    public static final EventService EVENT = load(EventService.class);

    public static final PlatformService PLATFORM = load(PlatformService.class);

    public static final RegistryService REGISTRY = load(RegistryService.class);

    public static final ServerNetworkingService SERVER_NETWORKING = load(ServerNetworkingService.class);

    public static <T> T load(Class<T> clazz) {
        var loadedService = ServiceLoader.load(clazz)
            .findFirst()
            .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));

        AVP.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);

        return loadedService;
    }
}
