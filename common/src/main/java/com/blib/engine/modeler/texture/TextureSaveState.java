package com.blib.engine.modeler.texture;

import com.mojang.blaze3d.platform.NativeImage;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.IdentityHashMap;

/**
 * Client-side save baseline for runtime textures. Textures are heap objects, so identity is the only stable key while
 * the modeler is open; each entry remembers the last-saved pixel snapshot and whether the current in-memory pixels have
 * diverged from it.
 */
@ApiStatus.Internal
public final class TextureSaveState {

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

    private static State stateFor(LoadedTexture texture) {
        return STATES.computeIfAbsent(texture, key -> new State(key.sourcePath(), snapshot(key)));
    }

    private static @Nullable PixelSnapshot snapshot(LoadedTexture texture) {
        var pixels = texture.texture().getPixels();
        return pixels == null ? null : PixelSnapshot.of(pixels);
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
