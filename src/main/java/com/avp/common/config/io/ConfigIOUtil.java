package com.avp.common.config.io;

import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class ConfigIOUtil {

    public static @NotNull Path resolveConfigPath(String name) {
        return FabricLoader.getInstance().getConfigDir().resolve("avp/" + name + ".config");
    }
}
