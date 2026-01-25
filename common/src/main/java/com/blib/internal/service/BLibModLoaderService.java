package com.blib.internal.service;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.api.common.mod.v1.model.DistributionEnvironmentType;
import com.blib.api.common.mod.v1.model.ReleaseEnvironmentType;
import com.blib.api.common.mod.v1.model.Version;
import com.blib.api.common.mod.v1.model.loader.ModLoaderType;

@ApiStatus.Internal
public interface BLibModLoaderService {

    ModLoaderType getModLoaderType();

    boolean isModLoaded(String modId);

    @Nullable
    Version getModVersion(String modId);

    DistributionEnvironmentType getDistributionEnvironmentType();

    ReleaseEnvironmentType getReleaseEnvironmentType();

}
