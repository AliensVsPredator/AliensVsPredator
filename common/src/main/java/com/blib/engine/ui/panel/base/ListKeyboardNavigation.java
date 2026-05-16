package com.blib.engine.ui.panel.base;

import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;

import com.blib.engine.ui.layout.ScrollViewport;
import com.blib.engine.ui.widget.ScrollContainer;

@ApiStatus.Internal
public final class ListKeyboardNavigation {

    private ListKeyboardNavigation() {}

    public static int directionForKey(int keyCode, int modifiers) {
        if (modifiers != 0) {
            return 0;
        }
        if (keyCode == GLFW.GLFW_KEY_UP) {
            return -1;
        }
        if (keyCode == GLFW.GLFW_KEY_DOWN) {
            return 1;
        }
        return 0;
    }

    public static int moveIndex(int currentIndex, int itemCount, int direction) {
        if (itemCount <= 0 || direction == 0) {
            return -1;
        }
        if (currentIndex < 0) {
            return direction > 0 ? 0 : itemCount - 1;
        }
        return Math.max(0, Math.min(itemCount - 1, currentIndex + direction));
    }

    public static void scrollRowIntoView(ScrollViewport scroll, int rowIndex, int rowHeight, int viewportHeight) {
        scrollRangeIntoView(scroll, rowIndex * rowHeight, rowIndex * rowHeight + rowHeight, viewportHeight);
    }

    public static void scrollRangeIntoView(ScrollViewport scroll, int rowTop, int rowBottom, int viewportHeight) {
        var viewTop = scroll.scrollY();
        var viewBottom = viewTop + Math.max(0, viewportHeight);
        if (rowTop < viewTop) {
            scroll.scrollBy(rowTop - viewTop);
        } else if (rowBottom > viewBottom) {
            scroll.scrollBy(rowBottom - viewBottom);
        }
    }

    public static void scrollRangeIntoView(ScrollContainer scroll, int rowTop, int rowBottom, int viewportHeight) {
        var viewTop = (int) scroll.scrollY();
        var viewBottom = viewTop + Math.max(0, viewportHeight);
        if (rowTop < viewTop) {
            scroll.scrollBy(rowTop - viewTop);
        } else if (rowBottom > viewBottom) {
            scroll.scrollBy(rowBottom - viewBottom);
        }
    }
}
