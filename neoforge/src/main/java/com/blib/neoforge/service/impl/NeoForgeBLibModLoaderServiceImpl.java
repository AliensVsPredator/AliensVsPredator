package com.blib.neoforge.service.impl;

import com.blib.common.model.DistributionEnvironmentType;
import com.blib.common.model.ReleaseEnvironmentType;
import com.blib.service.BLibModLoaderService;
import com.lib.common.util.Version;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import org.jetbrains.annotations.Nullable;

public class NeoForgeBLibModLoaderServiceImpl implements BLibModLoaderService {

    @Override
    public String getModLoaderName() {
        return "NeoForge";
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
