package com.avp.neoforge.service;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

import com.avp.service.PlatformService;

public class NeoForgePlatformService implements PlatformService {

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

}
