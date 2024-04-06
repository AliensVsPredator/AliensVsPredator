package org.avp.common.service;

import java.nio.file.Path;

public interface Platform {

    /**
     * Gets the config directory.
     *
     * @return The game config directory.
     */
    Path getConfigDirectory();

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Checks if the game is currently running client-side or server-side.
     *
     * @return True if running server-side, false otherwise.
     */
    boolean isServerEnvironment();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }

    Path getGameDirectory();
}
