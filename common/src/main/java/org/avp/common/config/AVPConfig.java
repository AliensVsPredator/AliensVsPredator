package org.avp.common.config;

import java.io.IOException;
import java.nio.file.Files;

import org.avp.api.common.config.Category;
import org.avp.api.common.config.Config;
import org.avp.api.common.config.ConfigParser;
import org.avp.common.AVPConstants;
import org.avp.api.service.Services;

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
                Whether xenomorphs will spawn in the overworld or not.
                Default Value: true
                """
        )
        public static boolean ENABLE_XENOMORPH_OVERWORLD_SPAWNS = true;

        @Config(value = "maxYLevelForBoilerSpawns", comment = "Default Value: 0")
        public static int MAX_Y_LEVEL_FOR_BOILER_SPAWNS = 0;

        @Config(value = "maxYLevelForCrusherSpawns", comment = "Default Value: 0")
        public static int MAX_Y_LEVEL_FOR_CRUSHER_SPAWNS = 0;

        @Config(value = "maxYLevelForDroneSpawns", comment = "Default Value: 75")
        public static int MAX_Y_LEVEL_FOR_DRONE_SPAWNS = 75;

        @Config(value = "maxYLevelForDroneRunnerSpawns", comment = "Default Value: 75")
        public static int MAX_Y_LEVEL_FOR_DRONE_RUNNER_SPAWNS = 75;

        @Config(value = "maxYLevelForQueenSpawns", comment = "Default Value: -24")
        public static int MAX_Y_LEVEL_FOR_QUEEN_SPAWNS = -24;

        @Config(value = "maxYLevelForPraetorianSpawns", comment = "Default Value: 0")
        public static int MAX_Y_LEVEL_FOR_PRAETORIAN_SPAWNS = 0;

        @Config(value = "maxYLevelForSpitterSpawns", comment = "Default Value: 0")
        public static int MAX_Y_LEVEL_FOR_SPITTER_SPAWNS = 0;

        @Config(value = "maxYLevelForWarriorSpawns", comment = "Default Value: 32")
        public static int MAX_Y_LEVEL_FOR_WARRIOR_SPAWNS = 32;

        @Config(value = "maxYLevelForWarriorRunnerSpawns", comment = "Default Value: 32")
        public static int MAX_Y_LEVEL_FOR_WARRIOR_RUNNER_SPAWNS = 32;
    }

    @Category("General")
    public static class General {

        @Config(
            value = "acidDamage",
            comment = """
                The amount of damage dealt by acid.
                Default Value: 2.0
                """
        )
        public static float ACID_DAMAGE = 2F;

        @Config(
            value = "gunsDoBlockDamage",
            comment = """
                Whether guns do block damage or not. Disabling this does not disable explosive block damage.
                Default Value: true
                """
        )
        public static boolean GUNS_DO_BLOCK_DAMAGE = true;
    }

    @Category("Predators")
    public static class Predators {

        @Config(
            value = "enableYautjaOverworldSpawns",
            comment = """
                Whether yautja (predators) will spawn in the overworld or not.
                Default Value: true
                """
        )
        public static boolean ENABLE_YAUTJA_OVERWORLD_SPAWNS = true;
    }

    public static void init() {
        var configFile = Services.PLATFORM_SERVICE.getConfigDirectory().resolve(AVPConstants.MOD_ID + ".properties");

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
