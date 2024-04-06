package org.avp.common.config;

import org.avp.api.config.Category;
import org.avp.api.config.Config;
import org.avp.api.config.ConfigParser;
import org.avp.common.AVPConstants;
import org.avp.common.service.Services;

import java.io.IOException;
import java.nio.file.Files;

/**
 * @author Boston Vanseghi
 * @author bright_spark
 */
public class AVPConfig {

    @Category("Aliens")
    public static class Aliens {
        @Config(
            value = "enableXenomorphOverworldSpawns",
            comment = """
        Whether xenomorphs will spawn in the overworld or not. Set to true by default.
        """
        )
        public static boolean ENABLE_XENOMORPH_OVERWORLD_SPAWNS = true;
    }

    @Category("Predators")
    public static class Predators {
        @Config(
            value = "enableYautjaOverworldSpawns",
            comment = """
        Whether yautja (predators) will spawn in the overworld or not. Set to true by default.
        """
        )
        public static boolean ENABLE_YAUTJA_OVERWORLD_SPAWNS = true;
    }

    public static void init() {
        var configFile = Services.PLATFORM.getConfigDirectory().resolve(AVPConstants.MOD_ID + ".properties");

        if (Files.exists(configFile)) {
            AVPConstants.LOGGER.info("Config file found. Attempting to read it...");
            try {
                ConfigParser.read(configFile, AVPConfig.class);
            } catch (IOException | IllegalAccessException e) {
                AVPConstants.LOGGER.error("Failed to read config file {}", configFile, e);
            }
        }

        // We always want to write the config back out to update it with added/removed properties.
        try {
            ConfigParser.write(configFile, AVPConfig.class);
        } catch (IOException | IllegalAccessException e) {
            AVPConstants.LOGGER.error("Failed to write config file {}", configFile, e);
        }
    }

    private AVPConfig() {
        throw new UnsupportedOperationException();
    }
}
