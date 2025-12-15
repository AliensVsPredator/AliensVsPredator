package com.blib.fabric.internal.service.impl;

import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.common.model.Version;
import com.blib.common.model.loader.ModLoaderType;
import com.blib.internal.service.BLibModLoaderService;
import com.blib.service.model.DistributionEnvironmentType;
import com.blib.service.model.ReleaseEnvironmentType;

@ApiStatus.Internal
public class FabricBLibModLoaderServiceImpl implements BLibModLoaderService {

    @Override
    public ModLoaderType getModLoaderType() {
        return ModLoaderType.FABRIC;
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
