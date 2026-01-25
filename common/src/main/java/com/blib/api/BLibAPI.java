package com.blib.api;

import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

import com.blib.api.common.mod.v1.BLibMod;
import com.blib.api.common.mod.v1.model.DistributionEnvironmentType;
import com.blib.api.common.mod.v1.model.ReleaseEnvironmentType;
import com.blib.api.common.mod.v1.model.Version;
import com.blib.api.common.mod.v1.model.loader.ModLoaderType;
import com.blib.internal.service.BLibInternalServices;

public final class BLibAPI {

    public static BLibMod createMod(String modId) {
        return new BLibMod(modId);
    }

    public static DistributionEnvironmentType getDistributionType() {
        return BLibInternalServices.MOD_LOADER.getDistributionEnvironmentType();
    }

    public static Path getGameDirectory() {
        return BLibInternalServices.MOD_LOADER.getGameDirectory();
    }

    public static ModLoaderType getModLoaderType() {
        return BLibInternalServices.MOD_LOADER.getModLoaderType();
    }

    public static @Nullable Version getModVersion(String modId) {
        return BLibInternalServices.MOD_LOADER.getModVersion(modId);
    }

    public static ReleaseEnvironmentType getReleaseEnvironmentType() {
        return BLibInternalServices.MOD_LOADER.getReleaseEnvironmentType();
    }

    public static boolean isDevelopmentEnvironment() {
        return BLibInternalServices.MOD_LOADER.isDevelopmentEnvironment();
    }

    public static boolean isModLoaded(String modId) {
        return BLibInternalServices.MOD_LOADER.isModLoaded(modId);
    }

    private BLibAPI() {
        throw new UnsupportedOperationException();
    }
}
