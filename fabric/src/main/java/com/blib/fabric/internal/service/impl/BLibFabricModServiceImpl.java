package com.blib.fabric.internal.service.impl;

import com.blib.BLibMod;
import com.blib.internal.service.BLibInternalServices;
import com.blib.internal.service.BLibModService;

public class BLibFabricModServiceImpl implements BLibModService {

    @Override
    public void postInitialize(BLibMod mod) {
        var registry = (FabricBLibRegistryServiceImpl) BLibInternalServices.REGISTRY;
        registry.finalize(mod);
    }
}
