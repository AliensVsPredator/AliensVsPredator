package com.blib.fabric.internal.service.impl;

import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.api.common.mod.v1.model.DistributionEnvironmentType;
import com.blib.api.common.mod.v1.model.ReleaseEnvironmentType;
import com.blib.api.common.mod.v1.model.Version;
import com.blib.api.common.mod.v1.model.loader.ModLoaderType;
import com.blib.internal.service.BLibModLoaderService;

@ApiStatus.Internal
public class BLibFabricModLoaderServiceImpl implements BLibModLoaderService {

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
