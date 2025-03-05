package com.avp.fabric.platform.service;

import com.avp.core.platform.service.PlatformService;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class FabricPlatformService implements PlatformService {

    @Override
    public @NotNull Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
