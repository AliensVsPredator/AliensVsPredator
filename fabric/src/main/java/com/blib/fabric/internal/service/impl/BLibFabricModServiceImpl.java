package com.blib.fabric.internal.service.impl;

import com.blib.BLibMod;
import com.blib.internal.service.BLibInternalServices;
import com.blib.internal.service.BLibModService;

public class BLibFabricModServiceImpl implements BLibModService {

    private static final FabricBLibRegistryServiceImpl REGISTRY = (FabricBLibRegistryServiceImpl) BLibInternalServices.REGISTRY;

    @Override
    public void postInitialize(BLibMod mod) {
        REGISTRY.finalize(mod);
    }
}
