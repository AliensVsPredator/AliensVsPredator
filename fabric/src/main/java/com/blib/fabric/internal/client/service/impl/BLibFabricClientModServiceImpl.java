package com.blib.fabric.internal.client.service.impl;

import org.jetbrains.annotations.ApiStatus;

import com.blib.client.BLibClientMod;
import com.blib.internal.client.service.BLibClientModService;

@ApiStatus.Internal
public class BLibFabricClientModServiceImpl implements BLibClientModService {

    @Override
    public void initialize(BLibClientMod mod, Runnable runnable) {
        runnable.run();

        BLibFabricClientModContainerLookup.INSTANCE.get(mod)
            .initialize();
    }
}
