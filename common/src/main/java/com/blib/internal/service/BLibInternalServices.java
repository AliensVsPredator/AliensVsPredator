package com.blib.internal.service;

public class BLibInternalServices {

    public static final BLibModService MOD = BLibServiceLoader.load(BLibModService.class);

    public static final BLibModLoaderService MOD_LOADER = BLibServiceLoader.load(BLibModLoaderService.class);

    public static final BLibRegistryService REGISTRY = BLibServiceLoader.load(BLibRegistryService.class);
}
