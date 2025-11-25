package com.blib.fabric.service.impl;

import com.blib.service.BLibModLoaderService;
import com.lib.common.util.Version;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;

public class FabricBLibModLoaderServiceImpl implements BLibModLoaderService {

    @Override
    public String getModLoaderName() {
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

    @Override
    public @Nullable Version getModVersion(String modId) {
        var container = FabricLoader.getInstance().getModContainer(modId);

        return container
            .map(modContainer -> modContainer.getMetadata().getVersion().getFriendlyString())
            .map(Version::parse)
            .orElse(null);
    }

}
