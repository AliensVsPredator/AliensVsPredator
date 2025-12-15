package com.blib.internal.service;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.common.mod.loader.model.ModLoaderType;
import com.blib.common.model.Version;
import com.blib.service.model.DistributionEnvironmentType;
import com.blib.service.model.ReleaseEnvironmentType;

@ApiStatus.Internal
public interface BLibModLoaderService {

    ModLoaderType getModLoaderType();

    boolean isModLoaded(String modId);

    @Nullable
    Version getModVersion(String modId);

    DistributionEnvironmentType getDistributionEnvironmentType();

    ReleaseEnvironmentType getReleaseEnvironmentType();

}
