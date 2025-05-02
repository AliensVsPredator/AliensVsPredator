package com.avp;

import mod.azure.azurelib.common.api.common.config.Config;
import mod.azure.azurelib.common.internal.common.config.ConfigHolder;
import mod.azure.azurelib.common.internal.common.config.ConfigHolderRegistry;
import mod.azure.azurelib.common.internal.common.config.format.ConfigFormats;
import mod.azure.azurelib.common.internal.common.config.format.IConfigFormatHandler;
import mod.azure.azurelib.common.internal.common.config.io.ConfigIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avp.common.block.TempAVPBlocks;
import com.avp.common.component.AVPDataComponents;
import com.avp.common.config.AVPConfig;
import com.avp.common.effect.AVPMobEffects;
import com.avp.common.item.TempAVPBlockItems;
import com.avp.common.item.TempAVPItems;
import com.avp.common.menu.AVPMenuTypes;
import com.avp.service.Services;

public class AVP {

    public static final String MOD_ID = "avp";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static AVPConfig config;

    public static void initialize() {
        AVP.config = registerConfig(AVPConfig.class, ConfigFormats.json()).getConfigInstance();

        LOGGER.info("Initializing AVP for platform '{}'", Services.PLATFORM.getPlatformName());

        TempAVPItems.initialize();
        TempAVPBlocks.initialize();
        TempAVPBlockItems.initialize();
        AVPDataComponents.initialize();
        AVPMenuTypes.initialize();
        AVPMobEffects.initialize();
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
