package com.blib.engine.modeler.texture;

import com.mojang.blaze3d.platform.NativeImage;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.IdentityHashMap;

import com.blib.engine.session.ProjectSession;
import com.blib.internal.common.storage.EngineProjectIO;

/**
 * Client-side save baseline for runtime textures. Textures are heap objects, so identity is the only stable key while
 * the modeler is open; each entry remembers the last-saved pixel snapshot and whether the current in-memory pixels have
 * diverged from it.
 */
@ApiStatus.Internal
public final class TextureSaveState {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextureSaveState.class);

    private static final IdentityHashMap<LoadedTexture, State> STATES = new IdentityHashMap<>();

    private TextureSaveState() {}

    public static synchronized void registerClean(LoadedTexture texture) {
        STATES.put(texture, new State(texture.sourcePath(), snapshot(texture)));
    }

    public static synchronized void unregister(LoadedTexture texture) {
        STATES.remove(texture);
    }

    public static synchronized void markClean(LoadedTexture texture) {
        var state = stateFor(texture);
        state.savedPixels = snapshot(texture);
        state.dirty = false;
    }

    public static synchronized void markDirty(LoadedTexture texture) {
        stateFor(texture).dirty = true;
    }

    public static synchronized void markPossiblyDirty(LoadedTexture texture) {
        var state = stateFor(texture);
        var current = snapshot(texture);
        state.dirty = state.savedPixels == null ? current != null : !state.savedPixels.matches(current);
    }

    public static synchronized boolean isDirty(LoadedTexture texture) {
        return stateFor(texture).dirty;
    }

    public static synchronized @Nullable Path savePath(LoadedTexture texture) {
        return stateFor(texture).savePath;
    }

    public static synchronized boolean canSave(LoadedTexture texture) {
        return stateFor(texture).savePath != null || hasProjectResourceSaveTarget(texture);
    }

    public static synchronized boolean save(LoadedTexture texture) {
        var path = savePath(texture);
        if (path == null) {
            path = resolveProjectResourceSavePath(texture);
            if (path == null) {
                return false;
            }
        }
        return saveAs(texture, path);
    }

    public static synchronized boolean saveAs(LoadedTexture texture, Path path) {
        var pixels = texture.texture().getPixels();
        if (pixels == null) {
            LOGGER.warn("TextureSaveState: cannot save {} because pixels are unavailable", texture.displayName());
            return false;
        }
        var target = ensurePngExtension(path).toAbsolutePath().normalize();
        var parent = target.getParent();
        try {
            if (parent != null) {
                Files.createDirectories(parent);
            }
            pixels.writeToFile(target);
        } catch (IOException | RuntimeException e) {
            LOGGER.warn("TextureSaveState: failed to save {} to {}: {}", texture.displayName(), target, e.getMessage());
            return false;
        }

        var state = stateFor(texture);
        state.savePath = target;
        state.savedPixels = PixelSnapshot.of(pixels);
        state.dirty = false;
        LOGGER.info("TextureSaveState: saved {} to {}", texture.displayName(), target);
        return true;
    }

    public static boolean hasProjectResourceSaveTarget(LoadedTexture texture) {
        return texture.sourceResource() != null && !ProjectSession.activeProjectName().isEmpty();
    }

    private static State stateFor(LoadedTexture texture) {
        return STATES.computeIfAbsent(texture, key -> new State(key.sourcePath(), snapshot(key)));
    }

    private static @Nullable PixelSnapshot snapshot(LoadedTexture texture) {
        var pixels = texture.texture().getPixels();
        return pixels == null ? null : PixelSnapshot.of(pixels);
    }

    private static Path ensurePngExtension(Path path) {
        var value = path.toString();
        return value.toLowerCase(java.util.Locale.ROOT).endsWith(".png") ? path : Path.of(value + ".png");
    }

    private static @Nullable Path resolveProjectResourceSavePath(LoadedTexture texture) {
        var resource = texture.sourceResource();
        var project = ProjectSession.activeProjectName();
        if (resource == null || project.isEmpty()) {
            return null;
        }

        var relPath = "assets/" + resource.getNamespace() + "/" + resource.getPath();
        try {
            return EngineProjectIO.prepareAssetPath(project, relPath);
        } catch (IOException e) {
            LOGGER.warn(
                "TextureSaveState: failed to prepare project resource-pack path for {} in project '{}': {}",
                resource,
                project,
                e.getMessage()
            );
            return null;
        }
    }

    private static final class State {

        private @Nullable Path savePath;

        private @Nullable PixelSnapshot savedPixels;

        private boolean dirty;

        private State(@Nullable Path savePath, @Nullable PixelSnapshot savedPixels) {
            this.savePath = savePath;
            this.savedPixels = savedPixels;
        }
    }

    private record PixelSnapshot(
        int width,
        int height,
        int[] pixels
    ) {

        private static PixelSnapshot of(NativeImage image) {
            var width = image.getWidth();
            var height = image.getHeight();
            var copy = new int[width * height];
            for (var y = 0; y < height; y++) {
                for (var x = 0; x < width; x++) {
                    copy[y * width + x] = image.getPixelRGBA(x, y);
                }
            }
            return new PixelSnapshot(width, height, copy);
        }

        private boolean matches(@Nullable PixelSnapshot other) {
            return other != null && width == other.width && height == other.height && Arrays.equals(pixels, other.pixels);
        }
    }
}
