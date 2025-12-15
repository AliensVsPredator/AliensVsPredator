package com.blib.internal.service;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.common.model.DistributionEnvironmentType;
import com.blib.common.model.ReleaseEnvironmentType;
import com.blib.common.model.Version;
import com.blib.common.model.loader.ModLoaderType;

@ApiStatus.Internal
public interface BLibModLoaderService {

    ModLoaderType getModLoaderType();

    boolean isModLoaded(String modId);

    @Nullable
    Version getModVersion(String modId);

    DistributionEnvironmentType getDistributionEnvironmentType();

    ReleaseEnvironmentType getReleaseEnvironmentType();

}
