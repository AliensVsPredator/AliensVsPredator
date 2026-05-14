package com.blib.engine.texture;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class TextureEditorState {

    private static TextureTool tool = TextureTool.SELECT;

    private static int primaryColor = 0xFF202020;

    private static @Nullable Selection selection;

    private TextureEditorState() {}

    public static TextureTool tool() {
        return tool;
    }

    public static void setTool(TextureTool nextTool) {
        tool = nextTool == null ? TextureTool.SELECT : nextTool;
    }

    public static int primaryColor() {
        return primaryColor;
    }

    public static void setPrimaryColor(int argb) {
        primaryColor = argb;
    }

    public static @Nullable Selection selection() {
        return selection;
    }

    public static void setSelection(int x0, int y0, int x1Exclusive, int y1Exclusive) {
        var minX = Math.min(x0, x1Exclusive);
        var minY = Math.min(y0, y1Exclusive);
        var maxX = Math.max(x0, x1Exclusive);
        var maxY = Math.max(y0, y1Exclusive);
        selection = maxX <= minX || maxY <= minY ? null : new Selection(minX, minY, maxX, maxY);
    }

    public static void clearSelection() {
        selection = null;
    }

    public static boolean containsSelectedPixel(int x, int y) {
        return selection == null || selection.contains(x, y);
    }

    public static int argbToNative(int argb) {
        return (argb & 0xFF00FF00) | ((argb & 0xFF) << 16) | ((argb >> 16) & 0xFF);
    }

    public record Selection(
        int x0,
        int y0,
        int x1Exclusive,
        int y1Exclusive
    ) {

        public int width() {
            return x1Exclusive - x0;
        }

        public int height() {
            return y1Exclusive - y0;
        }

        public boolean contains(int x, int y) {
            return x >= x0 && x < x1Exclusive && y >= y0 && y < y1Exclusive;
        }
    }
}
