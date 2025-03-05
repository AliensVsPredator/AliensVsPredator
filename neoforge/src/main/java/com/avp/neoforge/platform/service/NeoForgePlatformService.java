package com.avp.neoforge.platform.service;

import com.avp.core.platform.service.PlatformService;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class NeoForgePlatformService implements PlatformService {

    @Override
    public @NotNull Path getConfigDir() {
        return FMLLoader.getGamePath().resolve("config");
    }

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}
