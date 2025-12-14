package com.blib.neoforge.internal.service.impl;

import com.blib.common.model.Version;
import com.blib.internal.service.BLibModLoaderService;
import com.blib.mod.loader.model.ModLoaderType;
import com.blib.service.model.DistributionEnvironmentType;
import com.blib.service.model.ReleaseEnvironmentType;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class NeoForgeBLibModLoaderServiceImpl implements BLibModLoaderService {

    @Override
    public ModLoaderType getModLoaderType() {
        return ModLoaderType.NEOFORGE;
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public DistributionEnvironmentType getDistributionEnvironmentType() {
        return switch (FMLLoader.getDist()) {
            case CLIENT -> DistributionEnvironmentType.CLIENT;
            case DEDICATED_SERVER -> DistributionEnvironmentType.DEDICATED_SERVER;
        };
    }

    @Override
    public ReleaseEnvironmentType getReleaseEnvironmentType() {
        return !FMLLoader.isProduction()
            ? ReleaseEnvironmentType.DEVELOPMENT
            : ReleaseEnvironmentType.PRODUCTION;
    }

    @Override
    public @Nullable Version getModVersion(String modId) {
        return ModList.get()
            .getModContainerById(modId)
            .map(mod -> mod.getModInfo().getVersion().toString())
            .map(Version::parse)
            .orElse(null);
    }

}
