package com.blib.fabric.internal.service.impl;

import com.blib.BLibMod;
import com.blib.fabric.service.impl.FabricBLibRegistryServiceImpl;
import com.blib.internal.service.BLibModService;
import com.blib.service.BLibServices;

public class BLibFabricModServiceImpl implements BLibModService {

    private static final FabricBLibRegistryServiceImpl REGISTRY = (FabricBLibRegistryServiceImpl) BLibServices.REGISTRY;

    @Override
    public void postInitialize(BLibMod mod) {
        REGISTRY.finalize(mod);
    }
}
