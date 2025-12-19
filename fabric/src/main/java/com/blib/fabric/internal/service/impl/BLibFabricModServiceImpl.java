package com.blib.fabric.internal.service.impl;

import org.jetbrains.annotations.ApiStatus;

import com.blib.BLibMod;
import com.blib.internal.service.BLibInternalServices;
import com.blib.internal.service.BLibModService;

@ApiStatus.Internal
public class BLibFabricModServiceImpl implements BLibModService {

    @Override
    public void postInitialize(BLibMod mod) {
        var registry = (BLibFabricRegistryServiceImpl) BLibInternalServices.REGISTRY;
        registry.finalize(mod);
    }
}
