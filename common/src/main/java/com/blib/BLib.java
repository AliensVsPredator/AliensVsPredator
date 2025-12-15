package com.blib;

import com.blib.common.mod.loader.model.ModLoaderType;
import com.blib.common.model.Version;
import com.blib.common.network.BLibPacketDirections;
import com.blib.common.network.BLibServerPacketHandlers;
import com.blib.common.registry.init.BLibDataKeys;
import com.blib.internal.service.BLibInternalServices;
import com.blib.server.BlockBreakProgressManager;
import com.blib.server.ServerScheduler;
import com.blib.service.BLibServices;
import com.blib.service.model.DistributionEnvironmentType;
import com.blib.service.model.ReleaseEnvironmentType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BLib {

    public static final Logger LOGGER = LoggerFactory.getLogger(BLib.class);

    public static final String MOD_ID = "blib";

    public static final BLibMod MOD = createMod(MOD_ID);

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

    public static void initialize() {
        LOGGER.info("Initializing BLib for platform '{}'", BLib.getModLoaderType());

        MOD.initialize(() -> {
            BLibPacketDirections.initialize();
            BLibDataKeys.initialize();
            BLibServerPacketHandlers.initialize();
        });

        // TODO: There's a small bug here. This runs for both client and server levels!
        BLibServices.EVENT.afterLevelTick().register(ServerScheduler::tick);
        // TODO: There's a small bug here. This runs for both client and server levels!
        BLibServices.EVENT.afterLevelTick().register(BlockBreakProgressManager::tick);
    }
}
