package com.avp;

import com.blib.BLib;
import com.blib.BLibMod;
import com.blib.common.network.BLibPacketDirections;
import com.blib.common.network.BLibServerPacketHandlers;
import com.blib.common.registry.init.BLibDataKeys;
import com.blib.server.BlockBreakProgressManager;
import com.blib.server.ServerScheduler;
import com.blib.service.BLibServices;
import mod.azure.azurelib.common.config.Config;
import mod.azure.azurelib.common.config.ConfigHolder;
import mod.azure.azurelib.common.config.ConfigHolderRegistry;
import mod.azure.azurelib.common.config.format.ConfigFormats;
import mod.azure.azurelib.common.config.format.IConfigFormatHandler;
import mod.azure.azurelib.common.config.io.ConfigIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AVP {

    public static final String MOD_ID = "avp";

    public static final BLibMod MOD = BLib.createMod(MOD_ID);

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void initialize() {
        LOGGER.info("Initializing AVP for platform '{}'", BLib.getModLoaderType());

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

    /**
     * Registers your config class. Config will be immediately loaded upon calling.
     *
     * @param configClass   Your config class
     * @param formatFactory File format to be used by this config class. You can use values from {@link ConfigFormats}
     *                      for example.
     * @param <C>           Config type
     * @return Config holder containing your config instance. You obtain it by calling
     *         {@link ConfigHolder#getConfigInstance()} method.
     */
    @Deprecated(forRemoval = true)
    public static <C> ConfigHolder<C> registerConfig(Class<C> configClass, IConfigFormatHandler formatFactory) {
        var config = configClass.getAnnotation(Config.class);

        if (config == null) {
            throw new IllegalArgumentException("Config class must be annotated with '@Config' annotation");
        }

        var id = config.id();
        var filename = config.filename();

        if (filename.isEmpty()) {
            filename = id;
        }

        var group = config.group();

        if (group.isEmpty()) {
            group = id;
        }

        var holder = new ConfigHolder<>(configClass, id, filename, group, formatFactory);
        ConfigHolderRegistry.registerConfig(holder);

        if (configClass.getAnnotation(Config.NoAutoSync.class) == null) {
            ConfigIO.FILE_WATCH_MANAGER.addTrackedConfig(holder);
        }

        return holder;
    }
}
