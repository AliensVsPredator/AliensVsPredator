package com.blib.engine.ui;

import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL30;

/**
 * Downsamples the post-render main framebuffer (containing the world + HUD that vanilla just drew at full window size)
 * into the workspace's viewport panel rect. Game engines do this for their game-view tab so the editor sees a faithful
 * preview of the player's eye view without having to muck with the world's projection / glViewport mid-render —
 * everything the world camera and HUD code reads about the window stays at native size, and the only "shrink" is a
 * single bilinear-filtered framebuffer blit.
 * <p>
 * Algorithm:
 * <ol>
 * <li>Blit the main RT (full window) into an intermediate viewport-sized RT, with bilinear filtering. This is the
 * downsample step.</li>
 * <li>Blit the intermediate RT into the main RT at the viewport panel rect, 1:1. This paints the shrunk image where the
 * viewport panel will be.</li>
 * </ol>
 * Pixels outside the viewport rect on the main RT keep their original world+HUD content; the workspace's panels are
 * opaque, so they paint over that residue when the screen renders.
 * <p>
 * Called from {@link EngineWorkspaceScreen#render} after the world+HUD pass has finished (screen render runs after gui
 * render in vanilla's flow), so the main RT is guaranteed to hold the full-resolution player view by then.
 */
@ApiStatus.Internal
public final class EngineWorkspaceCompositor {

    private static @Nullable TextureTarget intermediate;

    /**
     * Offscreen target the engine renders a wrapped menu screen (TitleScreen, SelectWorldScreen, etc.) into when in
     * menu-overlay mode. Sized to the main RT so widgets lay out at their authored resolution; the result is blitted
     * (downsampled through {@link #intermediate}) into the viewport rect. Isolating the render in its own framebuffer
     * keeps wrapped-screen draws from leaking onto the main RT outside the viewport — important because some MC font
     * batches don't fully respect intermediate flushes, so isolation via FB binding is the only reliable barrier.
     */
    private static @Nullable TextureTarget wrappedScreenRT;

    private EngineWorkspaceCompositor() {}

    /**
     * Downsample-blit main RT into the viewport rect on main RT. {@code viewportY} is bottom-origin (GL convention),
     * matching {@code RenderSystem.viewport} args.
     */
    public static void composit(int viewportX, int viewportY, int viewportWidth, int viewportHeight) {
        if (viewportWidth <= 0 || viewportHeight <= 0) {
            return;
        }

        var mc = Minecraft.getInstance();
        var mainRT = mc.getMainRenderTarget();
        var fbWidth = mainRT.viewWidth;
        var fbHeight = mainRT.viewHeight;

        if (fbWidth <= 0 || fbHeight <= 0) {
            return;
        }

        if (intermediate == null) {
            intermediate = new TextureTarget(viewportWidth, viewportHeight, false, Minecraft.ON_OSX);
            intermediate.setClearColor(0f, 0f, 0f, 0f);
        } else if (intermediate.viewWidth != viewportWidth || intermediate.viewHeight != viewportHeight) {
            intermediate.resize(viewportWidth, viewportHeight, Minecraft.ON_OSX);
        }

        // Step 1: read = main, draw = intermediate. Linear filter for smooth downsample.
        GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, mainRT.frameBufferId);
        GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, intermediate.frameBufferId);
        GlStateManager._glBlitFrameBuffer(
            0,
            0,
            fbWidth,
            fbHeight,
            0,
            0,
            viewportWidth,
            viewportHeight,
            GL30.GL_COLOR_BUFFER_BIT,
            GL30.GL_LINEAR
        );

        // Step 2: read = intermediate, draw = main. 1:1 copy at viewport rect (bottom-origin).
        GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, intermediate.frameBufferId);
        GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, mainRT.frameBufferId);
        GlStateManager._glBlitFrameBuffer(
            0,
            0,
            viewportWidth,
            viewportHeight,
            viewportX,
            viewportY,
            viewportX + viewportWidth,
            viewportY + viewportHeight,
            GL30.GL_COLOR_BUFFER_BIT,
            GL30.GL_NEAREST
        );

        // Restore main RT as the active draw target so subsequent screen rendering goes there as expected. This also
        // resets RenderSystem.viewport to the main RT's full extent.
        mainRT.bindWrite(true);
    }

    /**
     * Allocate (or resize) {@link #wrappedScreenRT} to match the main RT's pixel size, clear it, and bind it as the
     * active draw target. Caller renders the wrapped screen into the now-bound RT, then calls
     * {@link #blitWrappedToViewport}. The clear-then-bind order matters: {@link TextureTarget#clear} ends with
     * {@code unbindWrite()}, so rebinding after the clear is required for subsequent draws to land in this RT.
     */
    public static void bindWrappedScreenTarget() {
        var mc = Minecraft.getInstance();
        var mainRT = mc.getMainRenderTarget();
        var fbWidth = Math.max(1, mainRT.viewWidth);
        var fbHeight = Math.max(1, mainRT.viewHeight);
        if (wrappedScreenRT == null) {
            wrappedScreenRT = new TextureTarget(fbWidth, fbHeight, true, Minecraft.ON_OSX);
            wrappedScreenRT.setClearColor(0f, 0f, 0f, 0f);
        } else if (wrappedScreenRT.viewWidth != fbWidth || wrappedScreenRT.viewHeight != fbHeight) {
            wrappedScreenRT.resize(fbWidth, fbHeight, Minecraft.ON_OSX);
        }
        wrappedScreenRT.clear(Minecraft.ON_OSX);
        wrappedScreenRT.bindWrite(true);
    }

    /**
     * Downsample-blit {@link #wrappedScreenRT} into the viewport rect on the main RT. Mirrors {@link #composit}'s
     * two-step path (full RT → intermediate viewport-size RT, intermediate → main RT viewport rect) so bilinear
     * filtering and pixel alignment match the in-world flow.
     */
    public static void blitWrappedToViewport(int viewportX, int viewportY, int viewportWidth, int viewportHeight) {
        if (viewportWidth <= 0 || viewportHeight <= 0 || wrappedScreenRT == null) {
            return;
        }
        var mc = Minecraft.getInstance();
        var mainRT = mc.getMainRenderTarget();

        if (intermediate == null) {
            intermediate = new TextureTarget(viewportWidth, viewportHeight, false, Minecraft.ON_OSX);
            intermediate.setClearColor(0f, 0f, 0f, 0f);
        } else if (intermediate.viewWidth != viewportWidth || intermediate.viewHeight != viewportHeight) {
            intermediate.resize(viewportWidth, viewportHeight, Minecraft.ON_OSX);
        }

        GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, wrappedScreenRT.frameBufferId);
        GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, intermediate.frameBufferId);
        GlStateManager._glBlitFrameBuffer(
            0,
            0,
            wrappedScreenRT.viewWidth,
            wrappedScreenRT.viewHeight,
            0,
            0,
            viewportWidth,
            viewportHeight,
            GL30.GL_COLOR_BUFFER_BIT,
            GL30.GL_LINEAR
        );

        GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, intermediate.frameBufferId);
        GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, mainRT.frameBufferId);
        GlStateManager._glBlitFrameBuffer(
            0,
            0,
            viewportWidth,
            viewportHeight,
            viewportX,
            viewportY,
            viewportX + viewportWidth,
            viewportY + viewportHeight,
            GL30.GL_COLOR_BUFFER_BIT,
            GL30.GL_NEAREST
        );

        mainRT.bindWrite(true);
    }

    public static void clear() {
        if (intermediate != null) {
            intermediate.destroyBuffers();
            intermediate = null;
        }
        if (wrappedScreenRT != null) {
            wrappedScreenRT.destroyBuffers();
            wrappedScreenRT = null;
        }
    }

    /**
     * Composit the main RT into a workspace-logical rect. Converts the logical rect (top-left origin, in workspace
     * logical pixels) to GL framebuffer coordinates (bottom-left origin, raw window pixels) using {@code logicalScale}
     * — the workspace's pose scale — and the current window's GUI scale, then calls {@link #composit}.
     */
    public static void compositWorldIntoLogicalRect(int rectX, int rectY, int rectW, int rectH, float logicalScale) {
        var raw = logicalRectToRawFramebuffer(rectX, rectY, rectW, rectH, logicalScale);
        composit(raw[0], raw[1], raw[2], raw[3]);
    }

    /**
     * Same conversion as {@link #compositWorldIntoLogicalRect} but sourcing pixels from the wrapped-screen offscreen RT
     * rather than the main RT.
     */
    public static void compositWrappedIntoLogicalRect(int rectX, int rectY, int rectW, int rectH, float logicalScale) {
        var raw = logicalRectToRawFramebuffer(rectX, rectY, rectW, rectH, logicalScale);
        blitWrappedToViewport(raw[0], raw[1], raw[2], raw[3]);
    }

    /** Returns {@code {x, y, w, h}} in raw bottom-origin framebuffer pixels for the given workspace-logical rect. */
    private static int[] logicalRectToRawFramebuffer(int rectX, int rectY, int rectW, int rectH, float logicalScale) {
        var window = Minecraft.getInstance().getWindow();
        var guiScale = window.getGuiScale();
        var rawWindowHeight = window.getHeight();

        var screenX = rectX * logicalScale;
        var screenY = rectY * logicalScale;
        var screenW = rectW * logicalScale;
        var screenH = rectH * logicalScale;

        var rawX = (int) Math.round(screenX * guiScale);
        var rawY = (int) Math.round(rawWindowHeight - (screenY + screenH) * guiScale);
        var rawW = (int) Math.round(screenW * guiScale);
        var rawH = (int) Math.round(screenH * guiScale);
        return new int[] { rawX, rawY, rawW, rawH };
    }
}
