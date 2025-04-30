package com.avp.fabric.service;

import net.fabricmc.loader.api.FabricLoader;

import com.avp.service.PlatformService;

public class FabricPlatformService implements PlatformService {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

}
