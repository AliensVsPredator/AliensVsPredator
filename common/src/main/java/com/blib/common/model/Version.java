package com.blib.common.model;

import org.jetbrains.annotations.Nullable;

public record Version(
    int major,
    int minor,
    int patch,
    @Nullable String suffix
) implements Comparable<Version> {

    public Version(int major, int minor, int patch) {
        this(major, minor, patch, null);
    }

    @Override
    public int compareTo(Version other) {
        if (major != other.major) {
            return Integer.compare(major, other.major);
        }

        if (minor != other.minor) {
            return Integer.compare(minor, other.minor);
        }

        if (patch != other.patch) {
            return Integer.compare(patch, other.patch);
        }

        // Release > pre-release
        if (suffix == null && other.suffix != null) {
            return 1;
        }

        if (suffix != null && other.suffix == null) {
            return -1;
        }

        if (suffix == null) {
            return 0;
        }

        // lexicographic comparison
        return suffix.compareTo(other.suffix);
    }

    public static Version parse(String input) {
        String versionPart;
        String suffix = null;

        var suffixIndex = input.indexOf('-');

        if (suffixIndex >= 0) {
            versionPart = input.substring(0, suffixIndex);
            suffix = input.substring(suffixIndex + 1);
        } else {
            versionPart = input;
        }

        var parts = versionPart.split("\\.");

        if (parts.length != 3) {
            throw new IllegalArgumentException("Version must be in the format major.minor.patch");
        }

        var major = Integer.parseInt(parts[0]);
        var minor = Integer.parseInt(parts[1]);
        var patch = Integer.parseInt(parts[2]);

        return new Version(major, minor, patch, suffix);
    }
}
