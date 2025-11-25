package com.blib.service;

import com.lib.common.util.Version;
import org.jetbrains.annotations.Nullable;

public interface BLibModLoaderService {

    /**
     * Gets the name of the current mod loader.
     *
     * @return The name of the current mod loader..
     */
    String getModLoaderName();

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
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }

    @Nullable
    Version getModVersion(String modId);
}
