package com.blib.engine.input;

import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Named, persisted set of user keybinding overrides. Sparse: only ids whose {@link Input} differs from the default
 * declared in {@link Keybindings} are stored. Resolution falls back to defaults for missing ids, so new bindings
 * shipped in future BLib releases automatically apply to existing profiles.
 * <p>
 * One file per profile under {@code <gameDir>/blib/engine/profiles/<id>.json}. The {@code id} matches the filename. The
 * "default" profile is special — always present, always selectable, never deletable, always has an empty overrides map
 * (it's the on-disk anchor for "use all defaults").
 */
@ApiStatus.Internal
public record KeybindingProfile(
    int version,
    String id,
    String displayName,
    Map<String, Input> overrides
) {

    public static final int CURRENT_VERSION = 1;

    public static final String DEFAULT_PROFILE_ID = "default";

    public static final String DEFAULT_PROFILE_DISPLAY_NAME = "Default";

    public static final Pattern ID_PATTERN = Pattern.compile("^[a-z0-9_-]{1,32}$");

    public static final int MAX_DISPLAY_NAME_LENGTH = 48;

    public static void validateId(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Profile id is empty");
        }
        if (!ID_PATTERN.matcher(id).matches()) {
            throw new IllegalArgumentException("Profile id must be 1–32 lowercase letters, digits, '_' or '-'");
        }
    }

    public static void validateDisplayName(String displayName) {
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("Display name is empty");
        }
        if (displayName.length() > MAX_DISPLAY_NAME_LENGTH) {
            throw new IllegalArgumentException("Display name must be ≤ " + MAX_DISPLAY_NAME_LENGTH + " chars");
        }
    }

    public static KeybindingProfile empty(String id, String displayName) {
        return new KeybindingProfile(CURRENT_VERSION, id, displayName, Map.of());
    }

    public static KeybindingProfile defaultProfile() {
        return empty(DEFAULT_PROFILE_ID, DEFAULT_PROFILE_DISPLAY_NAME);
    }

    public boolean isDefault() {
        return DEFAULT_PROFILE_ID.equals(id);
    }

    public KeybindingProfile withOverrides(Map<String, Input> newOverrides) {
        return new KeybindingProfile(version, id, displayName, Map.copyOf(newOverrides));
    }

    public KeybindingProfile withDisplayName(String newDisplayName) {
        return new KeybindingProfile(version, id, newDisplayName, overrides);
    }

    public KeybindingProfile withId(String newId) {
        return new KeybindingProfile(version, newId, displayName, overrides);
    }
}
