package com.blib.api.common.pathfinding.v1.feature;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Named feature presets exposed to tooling. A custom flag set is represented by a null profile on the navigator.
 */
public enum PathfindingProfile {
    BASIC_GROUND("Basic Ground", PathfindingFeatures.BASIC_GROUND),
    FLAT_ONLY("Flat Only", PathfindingFeatures.FLAT_ONLY),
    STAIRS_ONLY("Stairs Only", PathfindingFeatures.STAIRS_ONLY),
    LEGACY_PERMISSIVE("Legacy Permissive", PathfindingFeatures.LEGACY_PERMISSIVE);

    private final String displayName;

    private final PathfindingFeatures features;

    PathfindingProfile(String displayName, PathfindingFeatures features) {
        this.displayName = displayName;
        this.features = features;
    }

    public String displayName() {
        return displayName;
    }

    public PathfindingFeatures features() {
        return features;
    }

    public static Optional<PathfindingProfile> matching(PathfindingFeatures features) {
        for (var profile : values()) {
            if (profile.features().equals(features)) {
                return Optional.of(profile);
            }
        }

        return Optional.empty();
    }

    public static @Nullable PathfindingProfile fromOrdinal(int ordinal) {
        var values = values();

        if (ordinal < 0 || ordinal >= values.length) {
            return null;
        }

        return values[ordinal];
    }
}
