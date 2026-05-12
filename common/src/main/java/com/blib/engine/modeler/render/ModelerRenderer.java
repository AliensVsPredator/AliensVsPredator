package com.blib.engine.modeler.render;

import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import com.blib.engine.modeler.ModelerScene;

/**
 * Owns the offscreen framebuffer the modeler viewport renders into. Render-to-texture pattern (cf.
 * {@code JigsawPieceThumbnailCache}): save GL state, bind FBO, set projection / model-view from the modeler camera,
 * render scene, blit the FBO into the GUI rect.
 * <p>
 * <b>Resolution.</b> The FBO is sized to the panel's <em>raw-pixel</em> destination rect (logical pixels × GUI scale ×
 * {@code SUPERSAMPLE}) rather than logical pixels. Combined with {@code GL_LINEAR} downsample-blit, this gives
 * supersampled antialiasing for crisp edges and smooth grid lines — the simplest path to good quality without standing
 * up MSAA renderbuffers. {@code SUPERSAMPLE = 1} would mean exact 1:1 with the destination (no AA); 2 is a 4×-pixel
 * cost with markedly cleaner edges.
 */
@ApiStatus.Internal
public final class ModelerRenderer {

    private static final float BG_R = 0.07f;

    private static final float BG_G = 0.07f;

    private static final float BG_B = 0.08f;

    /** Render-resolution multiplier on top of GUI scale. 2 = 4× pixels per logical pixel (supersampled). */
    private static final int SUPERSAMPLE = 2;

    private @Nullable TextureTarget target;

    /**
     * Render the modeler scene into the panel at {@code (x, y, width, height)} in screen-logical pixels. The FBO is
     * sized to the underlying raw destination pixels (× {@link #SUPERSAMPLE}) so the blit downsamples cleanly.
     */
    public void render(GuiGraphics graphics, int x, int y, int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }

        // Compute the destination raw-pixel rect first — the size we want the FBO to match.
        var mc = Minecraft.getInstance();
        var mainRT = mc.getMainRenderTarget();
        var fbHeight = mainRT.viewHeight;

        var matrix = graphics.pose().last().pose();
        var tl = matrix.transformPosition(x, y, 0, new Vector3f());
        var br = matrix.transformPosition(x + width, y + height, 0, new Vector3f());
        var guiScale = mc.getWindow().getGuiScale();

        var dstX0 = (int) Math.round(tl.x * guiScale);
        var dstX1 = (int) Math.round(br.x * guiScale);
        var dstY0 = (int) Math.round(fbHeight - br.y * guiScale);
        var dstY1 = (int) Math.round(fbHeight - tl.y * guiScale);

        var dstW = Math.max(1, dstX1 - dstX0);
        var dstH = Math.max(1, dstY1 - dstY0);
        var fboW = dstW * SUPERSAMPLE;
        var fboH = dstH * SUPERSAMPLE;

        ensureTargetSize(fboW, fboH);
        if (target == null) {
            return;
        }

        renderScene(fboW, fboH);
        blitToGui(graphics, target, dstX0, dstY0, dstX1, dstY1);
    }

    private void ensureTargetSize(int width, int height) {
        if (target == null) {
            target = new TextureTarget(width, height, true, Minecraft.ON_OSX);
            target.setClearColor(BG_R, BG_G, BG_B, 1f);
            target.setFilterMode(GL11.GL_LINEAR);
        } else if (target.width != width || target.height != height) {
            target.resize(width, height, Minecraft.ON_OSX);
        }
    }

    private void renderScene(int width, int height) {
        var mc = Minecraft.getInstance();
        var mainRT = mc.getMainRenderTarget();

        // Save scissor + projection state. The GUI compositor's scissor would clip our FBO writes if we didn't
        // disable it for the duration of the offscreen pass.
        var savedProjection = RenderSystem.getProjectionMatrix();
        var savedSorting = RenderSystem.getVertexSorting();
        var wasScissor = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);
        int savedScissorX = 0;
        int savedScissorY = 0;
        int savedScissorW = 0;
        int savedScissorH = 0;
        if (wasScissor) {
            var box = new int[4];
            GL11.glGetIntegerv(GL11.GL_SCISSOR_BOX, box);
            savedScissorX = box[0];
            savedScissorY = box[1];
            savedScissorW = box[2];
            savedScissorH = box[3];
            RenderSystem.disableScissor();
        }

        target.bindWrite(true);
        GlStateManager._clearColor(BG_R, BG_G, BG_B, 1f);
        GlStateManager._clearDepth(1.0);
        GlStateManager._clear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);

        var scene = ModelerScene.get();
        var aspect = (float) width / (float) height;
        var projection = scene.camera.projectionMatrix(aspect);
        RenderSystem.setProjectionMatrix(projection, VertexSorting.DISTANCE_TO_ORIGIN);

        var modelview = RenderSystem.getModelViewStack();
        modelview.pushMatrix();
        modelview.identity();
        var camPos = scene.camera.position();
        var focus = new Vector3f((float) scene.camera.focusPoint.x, (float) scene.camera.focusPoint.y, (float) scene.camera.focusPoint.z);
        // Use the camera's own derived up vector — not constant world-up — so the orbit stays stable as pitch
        // approaches ±90° (the polar gimbal-lock case).
        modelview.lookAt(camPos, focus, scene.camera.up());
        RenderSystem.applyModelViewMatrix();

        try {
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(GL11.GL_LEQUAL);
            RenderSystem.disableCull();
            // Compat-profile niceties for cleaner lines. Drivers vary on whether these have an effect under
            // core-profile contexts, but they're harmless when unsupported.
            GL11.glEnable(GL13.GL_MULTISAMPLE);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);

            var pose = new Matrix4f();
            ModelerGridRenderer.render(pose);
            ModelerCubeRenderer.render(pose, scene.root, scene.selection);
        } finally {
            GL11.glDisable(GL11.GL_LINE_SMOOTH);

            modelview.popMatrix();
            RenderSystem.applyModelViewMatrix();

            target.unbindWrite();
            mainRT.bindWrite(true);
            RenderSystem.setProjectionMatrix(savedProjection, savedSorting);
            if (wasScissor) {
                RenderSystem.enableScissor(savedScissorX, savedScissorY, savedScissorW, savedScissorH);
            }
        }
    }

    private static void blitToGui(GuiGraphics graphics, TextureTarget src, int dstX0, int dstY0, int dstX1, int dstY1) {
        graphics.flush();

        var mainRT = Minecraft.getInstance().getMainRenderTarget();

        GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, src.frameBufferId);
        GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, mainRT.frameBufferId);
        // GL_LINEAR downsample from supersampled FBO → destination raw pixels. This is the antialiasing step.
        GlStateManager._glBlitFrameBuffer(
            0,
            0,
            src.viewWidth,
            src.viewHeight,
            dstX0,
            dstY0,
            dstX1,
            dstY1,
            GL30.GL_COLOR_BUFFER_BIT,
            GL30.GL_LINEAR
        );

        mainRT.bindWrite(true);
    }
}
