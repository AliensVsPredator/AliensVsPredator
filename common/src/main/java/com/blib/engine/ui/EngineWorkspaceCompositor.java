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

    public static void clear() {
        if (intermediate != null) {
            intermediate.destroyBuffers();
            intermediate = null;
        }
    }
}
