package com.blib.engine.modeler;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Wraps the LWJGL TinyFileDialogs native open-file dialog into a method that returns a {@link Path} (or {@code null} on
 * cancel / failure). Use this for the modeler's "Open Model from File…" entry — the user gets the OS-native file
 * browser they'd expect rather than an in-game list.
 * <p>
 * Caveats:
 * <ul>
 * <li>The native call <em>blocks</em> the calling thread until the user dismisses the dialog. That's fine for the
 * render-thread menu callback (Minecraft pauses anyway while a modal is up), but don't call it from a tick handler or
 * background worker — the game will freeze visibly.</li>
 * <li>On Linux the dialog falls back to {@code zenity} / {@code kdialog} / similar; if none are installed the call may
 * return null silently. Logged at INFO so the user can see why nothing opened.</li>
 * <li>Filter is permissive ({@code *.geo.json} + {@code *.json}) since some users save geo files with the bare
 * {@code .json} extension. The loader rejects malformed JSON regardless of extension.</li>
 * </ul>
 */
@ApiStatus.Internal
public final class ModelerFilePicker {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelerFilePicker.class);

    private ModelerFilePicker() {}

    /** Convenience overload — no initial directory hint, dialog opens wherever the OS defaults to. */
    public static @Nullable Path pickGeoModel() {
        return pickGeoModel(null);
    }

    /**
     * Prompt the user to pick a geo model file via the OS-native open-file dialog. Returns the chosen {@link Path} or
     * null if the user cancelled / the dialog couldn't be shown.
     * <p>
     * {@code initialDir}, when non-null and pointing at an existing directory, is passed to tinyfd as a "default path"
     * so the dialog opens there. The path is suffixed with the platform separator because tinyfd interprets
     * trailing-separator strings as a directory hint and other strings as a pre-filled filename — we want just the
     * directory. Falls back to the no-hint OS default when the dir is null or has since been moved/deleted.
     */
    public static @Nullable Path pickGeoModel(@Nullable Path initialDir) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            var filterPatterns = stack.mallocPointer(2);
            filterPatterns.put(stack.UTF8("*.geo.json"));
            filterPatterns.put(stack.UTF8("*.json"));
            filterPatterns.flip();

            var defaultPath = "";
            if (initialDir != null && Files.isDirectory(initialDir)) {
                defaultPath = initialDir.toAbsolutePath() + File.separator;
            }

            var picked = TinyFileDialogs.tinyfd_openFileDialog(
                "Open Geo Model",
                defaultPath,
                filterPatterns,
                "Bedrock Geo Models (*.geo.json, *.json)",
                false
            );

            if (picked == null || picked.isBlank()) {
                LOGGER.info("ModelerFilePicker: open-file dialog cancelled or unavailable");
                return null;
            }
            return Path.of(picked);
        } catch (RuntimeException e) {
            LOGGER.warn("ModelerFilePicker: native dialog threw {}", e.getMessage());
            return null;
        }
    }
}
