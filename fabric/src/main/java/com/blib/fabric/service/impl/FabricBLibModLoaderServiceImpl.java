package com.blib.fabric.service.impl;

import com.blib.common.model.Version;
import com.blib.service.BLibModLoaderService;
import com.blib.service.model.DistributionEnvironmentType;
import com.blib.service.model.ReleaseEnvironmentType;
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
    public @Nullable Version getModVersion(String modId) {
        var container = FabricLoader.getInstance().getModContainer(modId);

        return container
            .map(modContainer -> modContainer.getMetadata().getVersion().getFriendlyString())
            .map(Version::parse)
            .orElse(null);
    }

    @Override
    public DistributionEnvironmentType getDistributionEnvironmentType() {
        return switch (FabricLoader.getInstance().getEnvironmentType()) {
            case CLIENT -> DistributionEnvironmentType.CLIENT;
            case SERVER -> DistributionEnvironmentType.DEDICATED_SERVER;
        };
    }

    @Override
    public ReleaseEnvironmentType getReleaseEnvironmentType() {
        return FabricLoader.getInstance().isDevelopmentEnvironment()
            ? ReleaseEnvironmentType.DEVELOPMENT
            : ReleaseEnvironmentType.PRODUCTION;
    }
}
