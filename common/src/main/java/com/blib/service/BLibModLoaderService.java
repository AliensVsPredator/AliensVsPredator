package com.blib.service;

import com.blib.common.model.DistributionEnvironmentType;
import com.blib.common.model.ReleaseEnvironmentType;
import com.lib.common.util.Version;
import org.jetbrains.annotations.Nullable;

public interface BLibModLoaderService {

    /**
     * Gets the name of the current mod loader.
     *
     * @return The name of the current mod loader.
     */
    String getModLoaderName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    @Nullable
    Version getModVersion(String modId);

    DistributionEnvironmentType getDistributionEnvironmentType();

    ReleaseEnvironmentType getReleaseEnvironmentType();

}
