package com.blib.engine.texture;

import com.mojang.blaze3d.platform.NativeImage;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayDeque;

import com.blib.engine.modeler.history.ModelerAction;
import com.blib.engine.modeler.history.ModelerActionHistory;
import com.blib.engine.modeler.texture.LoadedTexture;

@ApiStatus.Internal
public final class TexturePaintOps {

    private TexturePaintOps() {}

    public static void paintLine(LoadedTexture texture, NativeImage pixels, int x0, int y0, int x1, int y1) {
        paintLine(texture, pixels, x0, y0, x1, y1, true);
    }

    public static void paintLine(
        LoadedTexture texture,
        NativeImage pixels,
        int x0,
        int y0,
        int x1,
        int y1,
        boolean respectSelection
    ) {
        var nativeColor = TextureEditorState.argbToNative(TextureEditorState.primaryColor());
        var dx = Math.abs(x1 - x0);
        var dy = Math.abs(y1 - y0);
        var sx = x0 < x1 ? 1 : -1;
        var sy = y0 < y1 ? 1 : -1;
        var err = dx - dy;
        var x = x0;
        var y = y0;
        while (true) {
            if (x >= 0 && x < pixels.getWidth() && y >= 0 && y < pixels.getHeight() && containsSelectedPixel(x, y, respectSelection)) {
                pixels.setPixelRGBA(x, y, nativeColor);
            }
            if (x == x1 && y == y1) {
                break;
            }
            var e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
        texture.texture().upload();
    }

    public static boolean bucketFill(LoadedTexture texture, NativeImage pixels, int startX, int startY) {
        return bucketFill(texture, pixels, startX, startY, true);
    }

    public static boolean bucketFill(LoadedTexture texture, NativeImage pixels, int startX, int startY, boolean respectSelection) {
        if (!containsSelectedPixel(startX, startY, respectSelection)) {
            return false;
        }
        var replacement = TextureEditorState.argbToNative(TextureEditorState.primaryColor());
        var target = pixels.getPixelRGBA(startX, startY);
        if (target == replacement) {
            return false;
        }
        var changed = false;
        var width = pixels.getWidth();
        var height = pixels.getHeight();
        var visited = new boolean[width * height];
        var queue = new ArrayDeque<Integer>();
        queue.add(startY * width + startX);
        while (!queue.isEmpty()) {
            var packed = queue.removeFirst();
            var x = packed % width;
            var y = packed / width;
            var idx = y * width + x;
            if (visited[idx]) {
                continue;
            }
            visited[idx] = true;
            if (!containsSelectedPixel(x, y, respectSelection) || pixels.getPixelRGBA(x, y) != target) {
                continue;
            }
            pixels.setPixelRGBA(x, y, replacement);
            changed = true;
            if (x > 0) {
                queue.add(idx - 1);
            }
            if (x + 1 < width) {
                queue.add(idx + 1);
            }
            if (y > 0) {
                queue.add(idx - width);
            }
            if (y + 1 < height) {
                queue.add(idx + width);
            }
        }
        if (changed) {
            texture.texture().upload();
        }
        return changed;
    }

    private static boolean containsSelectedPixel(int x, int y, boolean respectSelection) {
        return !respectSelection || TextureEditorState.containsSelectedPixel(x, y);
    }

    public static void pushTexturePixelsAction(
        String typeId,
        String description,
        LoadedTexture texture,
        ModelerAction.TexturePixelsMemento before,
        ModelerAction.TexturePixelsMemento after
    ) {
        if (!before.differsFrom(after)) {
            return;
        }
        ModelerActionHistory.push(
            new ModelerAction.TexturePixelsAction(
                typeId,
                description + " " + texture.displayName(),
                System.currentTimeMillis(),
                texture,
                before,
                after
            )
        );
    }
}
