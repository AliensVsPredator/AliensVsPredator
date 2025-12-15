package com.blib.config;

import mod.azure.azurelib.common.config.Config;
import mod.azure.azurelib.common.config.ConfigHolder;
import mod.azure.azurelib.common.config.ConfigHolderRegistry;
import mod.azure.azurelib.common.config.format.ConfigFormats;
import mod.azure.azurelib.common.config.format.IConfigFormatHandler;
import mod.azure.azurelib.common.config.io.ConfigIO;

public class BLibConfigs {

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
    public static <C> ConfigHolder<C> register(Class<C> configClass, IConfigFormatHandler formatFactory) {
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
