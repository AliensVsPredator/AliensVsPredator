package com.blib.engine.ui;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;

/**
 * OS cursor swap for the engine workspace. Shows a crosshair while the user has a jigsaw piece on the cursor (and the
 * cursor is over the viewport rect); restores the default arrow everywhere else. Lazy GLFW handle creation; one cursor
 * object is allocated at most for the JVM lifetime.
 */
@ApiStatus.Internal
public final class EngineCursor {

    private static long crosshairHandle = 0L;

    private static boolean crosshairActive = false;

    private EngineCursor() {}

    /**
     * Set crosshair if {@code wantCrosshair} is true and the cursor is inside the viewport rect; otherwise restore
     * the default. Idempotent — repeated calls in the same state are no-ops, so it's safe to call every frame from
     * {@code render}.
     */
    public static void update(int logicalMouseX, int logicalMouseY, int viewX, int viewY, int viewWidth, int viewHeight, boolean wantCrosshair) {
        boolean overViewport = logicalMouseX >= viewX
            && logicalMouseX < viewX + viewWidth
            && logicalMouseY >= viewY
            && logicalMouseY < viewY + viewHeight;

        if (wantCrosshair && overViewport) {
            ensureCrosshair();
        } else {
            resetIfActive();
        }
    }

    /** Restore the default cursor. Called from {@code removed()} when the workspace closes. */
    public static void reset() {
        resetIfActive();
    }

    private static void ensureCrosshair() {
        if (crosshairActive) {
            return;
        }
        var window = Minecraft.getInstance().getWindow();
        if (crosshairHandle == 0L) {
            crosshairHandle = GLFW.glfwCreateStandardCursor(GLFW.GLFW_CROSSHAIR_CURSOR);
        }
        if (crosshairHandle != 0L) {
            GLFW.glfwSetCursor(window.getWindow(), crosshairHandle);
            crosshairActive = true;
        }
    }

    private static void resetIfActive() {
        if (!crosshairActive) {
            return;
        }
        var window = Minecraft.getInstance().getWindow();
        GLFW.glfwSetCursor(window.getWindow(), 0L);
        crosshairActive = false;
    }
}
