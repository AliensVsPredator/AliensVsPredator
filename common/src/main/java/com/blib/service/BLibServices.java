package com.blib.service;

import com.blib.internal.service.BLibServiceLoader;

public class BLibServices {

    public static final BLibClientNetworkingService CLIENT_NETWORKING = BLibServiceLoader.load(BLibClientNetworkingService.class);

    public static final BLibClientRegistryService CLIENT_REGISTRY = BLibServiceLoader.load(BLibClientRegistryService.class);

    public static final BLibEventService EVENT = BLibServiceLoader.load(BLibEventService.class);

    public static final BLibFactoryService FACTORY = BLibServiceLoader.load(BLibFactoryService.class);

    public static final BLibServerNetworkingService SERVER_NETWORKING = BLibServiceLoader.load(BLibServerNetworkingService.class);

}
