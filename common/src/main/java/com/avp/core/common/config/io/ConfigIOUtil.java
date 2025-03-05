package com.avp.core.common.config.io;

import com.avp.core.platform.service.Services;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class ConfigIOUtil {

    public static @NotNull Path resolveConfigPath(String name) {
        return Services.PLATFORM.getConfigDir().resolve("avp/" + name + ".config");
    }
}
