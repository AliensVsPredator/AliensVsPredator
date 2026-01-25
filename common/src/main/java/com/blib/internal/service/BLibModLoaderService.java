package com.blib.internal.service;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

import com.blib.api.common.mod.v1.model.DistributionEnvironmentType;
import com.blib.api.common.mod.v1.model.ReleaseEnvironmentType;
import com.blib.api.common.mod.v1.model.Version;
import com.blib.api.common.mod.v1.model.loader.ModLoaderType;

@ApiStatus.Internal
public interface BLibModLoaderService {

    Path getGameDirectory();

    ModLoaderType getModLoaderType();

    boolean isModLoaded(String modId);

    @Nullable
    Version getModVersion(String modId);

    DistributionEnvironmentType getDistributionEnvironmentType();

    ReleaseEnvironmentType getReleaseEnvironmentType();

    default boolean isDevelopmentEnvironment() {
        return getReleaseEnvironmentType() == ReleaseEnvironmentType.DEVELOPMENT;
    }
}
