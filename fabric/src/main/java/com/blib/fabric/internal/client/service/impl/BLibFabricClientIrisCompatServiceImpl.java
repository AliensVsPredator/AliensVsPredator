package com.blib.fabric.internal.client.service.impl;

import net.fabricmc.loader.api.FabricLoader;

import com.blib.internal.client.service.BLibClientIrisCompatService;

public final class BLibFabricClientIrisCompatServiceImpl implements BLibClientIrisCompatService {

    @Override
    public boolean isShaderModActive() {
        var loader = FabricLoader.getInstance();
        return loader.isModLoaded("iris") || loader.isModLoaded("oculus");
    }
}
