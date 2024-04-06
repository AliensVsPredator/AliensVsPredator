package org.avp.common.config;

import org.avp.api.config.Config;
import org.avp.api.config.ConfigParser;
import org.avp.common.AVPConstants;
import org.avp.common.service.Services;

import java.io.IOException;
import java.nio.file.Files;

public class AVPConfig {
    @Config(
        value = "enableXenomorphOverworldSpawns",
        comment = """
        Whether xenomorphs will spawn in the overworld or not. Set to true by default.
        """
    )
    public static boolean ENABLE_XENOMORPH_OVERWORLD_SPAWNS = true;

    public static void init() {
        var configFile = Services.PLATFORM.getConfigDirectory().resolve(AVPConstants.MOD_ID + ".properties");

        if (Files.exists(configFile)) {
            AVPConstants.LOGGER.info("Config file found");
            try {
                ConfigParser.read(configFile, AVPConfig.class);
            } catch (IOException | IllegalAccessException e) {
                AVPConstants.LOGGER.error("Failed to read config file {}", configFile, e);
            }
        } else {
            AVPConstants.LOGGER.info("No config file found - creating it!");
            try {
                ConfigParser.write(configFile, AVPConfig.class);
            } catch (IOException | IllegalAccessException e) {
                AVPConstants.LOGGER.error("Failed to write config file {}", configFile, e);
            }
        }
    }
}
