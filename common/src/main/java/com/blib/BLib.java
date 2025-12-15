package com.blib;

import com.blib.common.mod.loader.model.ModLoaderType;
import com.blib.common.model.Version;
import com.blib.internal.service.BLibInternalServices;
import com.blib.service.model.DistributionEnvironmentType;
import com.blib.service.model.ReleaseEnvironmentType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BLib {

    public static final Logger LOGGER = LoggerFactory.getLogger(BLib.class);

    public static final BLibMod MOD = createMod("blib");

    public static BLibMod createMod(String modId) {
        return new BLibMod(modId);
    }

    public static DistributionEnvironmentType getDistributionType() {
        return BLibInternalServices.MOD_LOADER.getDistributionEnvironmentType();
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

    public static boolean isModLoaded(String modId) {
        return BLibInternalServices.MOD_LOADER.isModLoaded(modId);
    }
}
