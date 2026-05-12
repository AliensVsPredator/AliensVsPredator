package com.blib.engine.input;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Single facade the workspace and preferences UI talk to for keybinding-profile discovery, selection, and persistence.
 * Mirrors {@code LayoutCatalog}'s structure (lazy on-demand listing; {@link #initialize} seeds the default profile and
 * loads the active one). The currently-active profile is also pushed into {@link ActiveKeybindings} so handler
 * resolution stays consistent.
 */
@ApiStatus.Internal
public final class KeybindingProfileCatalog {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeybindingProfileCatalog.class);

    private KeybindingProfileCatalog() {}

    /**
     * One-shot init: seed the default profile if missing, read the active-profile pointer, load that profile, and push
     * it to {@link ActiveKeybindings}. Falls back to the default profile if the pointed-at profile is missing or
     * corrupt. Safe to call repeatedly.
     */
    public static void initialize() {
        try {
            KeybindingProfileStorage.seedDefaultIfMissing();
        } catch (IOException e) {
            LOGGER.warn("[BLib] KeybindingProfileCatalog.initialize: seed failed", e);
        }
        var activeId = KeybindingProfileStorage.readActiveProfileId().orElse(KeybindingProfile.DEFAULT_PROFILE_ID);
        var loaded = KeybindingProfileStorage.read(activeId).orElseGet(() -> {
            if (!KeybindingProfile.DEFAULT_PROFILE_ID.equals(activeId)) {
                LOGGER.warn("[BLib] KeybindingProfileCatalog: active profile '{}' missing; falling back to default", activeId);
            }
            return KeybindingProfile.defaultProfile();
        });
        ActiveKeybindings.setActive(loaded);
    }

    public static List<KeybindingProfile> listAll() {
        return KeybindingProfileStorage.list();
    }

    public static @Nullable KeybindingProfile get(String id) {
        return KeybindingProfileStorage.read(id).orElse(null);
    }

    public static KeybindingProfile getActive() {
        return ActiveKeybindings.getActive();
    }

    /**
     * Set the active profile by id. Reads from disk, updates the pointer file, and notifies {@link ActiveKeybindings}.
     * Falls back to the default profile if the id points at a missing or unparseable file.
     */
    public static void setActive(String id) {
        var profile = KeybindingProfileStorage.read(id).orElseGet(() -> {
            LOGGER.warn("[BLib] KeybindingProfileCatalog.setActive: profile '{}' missing; falling back to default", id);
            return KeybindingProfile.defaultProfile();
        });
        try {
            KeybindingProfileStorage.writeActiveProfileId(profile.id());
        } catch (IOException e) {
            LOGGER.warn("[BLib] KeybindingProfileCatalog.setActive: failed to write pointer", e);
        }
        ActiveKeybindings.setActive(profile);
    }

    /** Persist {@code profile} and refresh {@link ActiveKeybindings} if it matches the currently-active id. */
    public static void save(KeybindingProfile profile) throws IOException {
        KeybindingProfileStorage.write(profile);
        if (ActiveKeybindings.getActive().id().equals(profile.id())) {
            ActiveKeybindings.setActive(profile);
        }
    }

    /**
     * Delete a non-default profile. If it was active, switches to default. Returns true if anything was deleted.
     */
    public static boolean delete(String id) throws IOException {
        if (KeybindingProfile.DEFAULT_PROFILE_ID.equals(id)) {
            return false;
        }
        var wasActive = ActiveKeybindings.getActive().id().equals(id);
        var deleted = KeybindingProfileStorage.delete(id);
        if (deleted && wasActive) {
            setActive(KeybindingProfile.DEFAULT_PROFILE_ID);
        }
        return deleted;
    }

    public static boolean idAvailable(String id) {
        try {
            KeybindingProfile.validateId(id);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return !KeybindingProfileStorage.exists(id);
    }

    /** Slugify {@code displayName} into a profile id, deduplicating with {@code _2}, {@code _3}, … if needed. */
    public static String suggestId(String displayName) {
        var base = slugify(displayName);
        if (base.isEmpty()) {
            base = "profile";
        }
        if (base.length() > 32) {
            base = base.substring(0, 32);
        }
        if (idAvailable(base)) {
            return base;
        }
        for (var i = 2; i < Integer.MAX_VALUE; i++) {
            var suffix = "_" + i;
            var maxBaseLen = Math.max(1, 32 - suffix.length());
            var trimmed = base.length() > maxBaseLen ? base.substring(0, maxBaseLen) : base;
            var candidate = trimmed + suffix;
            if (idAvailable(candidate)) {
                return candidate;
            }
        }
        return (base + "_" + System.currentTimeMillis()).substring(0, Math.min(32, base.length() + 14));
    }

    private static String slugify(String s) {
        if (s == null) {
            return "";
        }
        var lower = s.toLowerCase(Locale.ROOT);
        var sb = new StringBuilder(lower.length());
        var lastWasUnderscore = false;
        for (var i = 0; i < lower.length(); i++) {
            var ch = lower.charAt(i);
            var ok = (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || ch == '_' || ch == '-';
            if (ok) {
                sb.append(ch);
                lastWasUnderscore = (ch == '_');
            } else if (!lastWasUnderscore) {
                sb.append('_');
                lastWasUnderscore = true;
            }
        }
        var slug = sb.toString();
        var start = 0;
        while (start < slug.length() && (slug.charAt(start) == '_' || slug.charAt(start) == '-')) {
            start++;
        }
        var end = slug.length();
        while (end > start && (slug.charAt(end - 1) == '_' || slug.charAt(end - 1) == '-')) {
            end--;
        }
        return slug.substring(start, end);
    }
}
