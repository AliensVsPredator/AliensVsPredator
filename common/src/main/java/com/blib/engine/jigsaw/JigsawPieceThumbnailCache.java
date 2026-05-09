package com.blib.engine.jigsaw;

import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.util.LinkedHashMap;
import java.util.Map;

import com.blib.api.common.worldgen.v1.StructureTemplateAccessor;

/**
 * LRU cache of pre-rendered piece thumbnails. The first time a card is requested, the structure's blocks are rendered
 * into a dedicated {@link TextureTarget} (one-time cost per template); every subsequent frame the panel just
 * framebuffer- blits the cached target into the main RT at the requested rect via {@code glBlitFrameBuffer}
 * (essentially free).
 * <p>
 * Without this cache the panel was issuing tens of thousands of {@code BlockRenderDispatcher.renderSingleBlock} calls
 * per frame for the visible cards, which dominated frame time.
 * <p>
 * Caching is keyed by {@link ResourceLocation}, sized to a fixed pixel resolution, and bounded by an LRU cap so memory
 * doesn't grow unbounded as the user scrolls through hundreds of templates. To avoid frame stutters when many new cards
 * become visible at once (e.g. on initial open or after a search-text change), at most {@link #MAX_RENDERS_PER_FRAME}
 * fresh thumbnails are rendered per frame; cards beyond that draw a placeholder until later frames catch up.
 */
@ApiStatus.Internal
public final class JigsawPieceThumbnailCache {

    /** Hard cap on cached thumbnails. ~128 × 256×256 RGBA + depth ≈ 64 MB of GPU memory worst-case. */
    private static final int MAX_ENTRIES = 128;

    /**
     * Pixel size of each thumbnail's framebuffer. Sized large enough to cover typical GUI-scale × workspace-scale
     * combos with a clean downscale (avoids the blurry upscale we'd get from a smaller source). At GUI scale 4 the
     * destination card rect lands around 126 fb-px; 256 source gives ~2× downsample headroom into GUI scales 6–7 before
     * the blit starts upscaling and showing pixelation.
     */
    private static final int THUMBNAIL_PIXEL_SIZE = 256;

    /**
     * Per-frame render budget. With a cold cache (e.g. user just opened the workspace) and 24 visible cards, this
     * spreads the work over ~6 frames so the user doesn't see one big stutter — instead thumbnails progressively fade
     * in over a fraction of a second.
     */
    private static final int MAX_RENDERS_PER_FRAME = 4;

    /** Background color matching the panel's card BG, so the thumbnail's letterbox area blends with the card frame. */
    private static final float BG_R = 0x1A / 255f;

    private static final float BG_G = 0x1A / 255f;

    private static final float BG_B = 0x22 / 255f;

    private static final float CARD_PERSPECTIVE_PITCH = 30f;

    /**
     * Yaw chosen so the template's -Z face (the conventional "front" in MC structures, towards north) is visible to the
     * right of frame. Was {@code -45} initially, which showed the +Z (back) face — visually the structure looked
     * inside-out / mirrored. {@code 135} = {@code -45 + 180} faces the camera at the front-corner instead.
     */
    private static final float CARD_PERSPECTIVE_YAW = 135f;

    /** Access-ordered LinkedHashMap so iteration starts at the least-recently-used entry (eviction target). */
    private static final Map<ResourceLocation, TextureTarget> cache = new LinkedHashMap<>(16, 0.75f, true);

    private static int rendersThisFrame;

    private JigsawPieceThumbnailCache() {}

    /**
     * Reset the per-frame render budget. Call once at the start of each panel render — the first card-draw call will
     * begin spending the budget; subsequent calls within the same frame share it.
     */
    public static void beginFrame() {
        rendersThisFrame = 0;
    }

    /**
     * Render the cached thumbnail for {@code template} into the given GUI rect. If the thumbnail isn't cached and the
     * per-frame render budget hasn't been exhausted, render it now and cache the result. If the budget is exhausted,
     * draw a placeholder; a future frame will populate the cache.
     */
    public static void draw(
        GuiGraphics graphics,
        ResourceLocation id,
        StructureTemplate template,
        int x,
        int y,
        int width,
        int height
    ) {
        var target = cache.get(id);
        if (target == null) {
            if (rendersThisFrame >= MAX_RENDERS_PER_FRAME) {
                drawPlaceholder(graphics, x, y, width, height);
                return;
            }
            target = new TextureTarget(THUMBNAIL_PIXEL_SIZE, THUMBNAIL_PIXEL_SIZE, true, Minecraft.ON_OSX);
            try {
                renderTemplateIntoTarget(target, template);
                cache.put(id, target);
                rendersThisFrame++;
                evictIfNeeded();
            } catch (Throwable t) {
                // Defensive: a render-to-target hiccup (driver state, mod conflict) shouldn't crash the workspace.
                // Drop the partially-initialized target and fall back to a placeholder so the panel keeps working.
                target.destroyBuffers();
                drawPlaceholder(graphics, x, y, width, height);
                return;
            }
        }
        blitTargetIntoGui(graphics, target, x, y, width, height);
    }

    /** Free every cached target. Called when the workspace closes so we don't leak GPU memory across sessions. */
    public static void clear() {
        for (var target : cache.values()) {
            target.destroyBuffers();
        }
        cache.clear();
    }

    private static void evictIfNeeded() {
        while (cache.size() > MAX_ENTRIES) {
            var iter = cache.entrySet().iterator();
            var oldest = iter.next();
            iter.remove();
            oldest.getValue().destroyBuffers();
        }
    }

    /**
     * Bind {@code target}, set up a GUI-style ortho projection at thumbnail resolution, and submit the template's
     * blocks through {@link net.minecraft.client.renderer.block.BlockRenderDispatcher}. Restores the main RT, the
     * previous projection / modelview matrices, and the previous scissor state on exit so the surrounding GUI render is
     * unaffected.
     * <p>
     * Scissor handling is the subtle bit: the panel calls us with a scissor active that clips draws to its grid rect in
     * main-framebuffer pixel coords. If we left that scissor on after switching to our 128×128 offscreen target, both
     * {@code glClear} and the block-rendering fragments would be scissored out (the saved rect is far outside our
     * target's bounds), and the target would read back as uninitialized black. So we save+disable scissor for the
     * duration of the render-to-target and restore it before returning.
     */
    private static void renderTemplateIntoTarget(TextureTarget target, StructureTemplate template) {
        var mc = Minecraft.getInstance();
        var mainRT = mc.getMainRenderTarget();

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

        GlStateManager._clearColor(BG_R, BG_G, BG_B, 1.0f);
        GlStateManager._clearDepth(1.0);
        GlStateManager._clear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);

        // GUI-style ortho with a wide Z range so the pose stack's local Z translations (and post-rotation Z spread)
        // never z-clip. Mirrors what GuiGraphics's setup uses internally so the BlockRenderDispatcher's pose math
        // behaves the same as it does in the live HUD path.
        var ortho = new Matrix4f().setOrtho(0f, THUMBNAIL_PIXEL_SIZE, THUMBNAIL_PIXEL_SIZE, 0f, 1000f, 21000f);
        RenderSystem.setProjectionMatrix(ortho, VertexSorting.ORTHOGRAPHIC_Z);

        var modelview = RenderSystem.getModelViewStack();
        modelview.pushMatrix();
        modelview.identity();
        // GUI convention: translate -11000 along Z so pose-Z=0 lands centered in the projection's Z range, leaving
        // ~10000 units of headroom on either side for the structure's depth after rotation/scale.
        modelview.translate(0f, 0f, -11000f);
        RenderSystem.applyModelViewMatrix();

        try {
            renderBlocks(template);
        } finally {
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

    private static void renderBlocks(StructureTemplate template) {
        var size = template.getSize();
        if (size.getX() <= 0 || size.getY() <= 0 || size.getZ() <= 0) {
            return;
        }

        var palettes = ((StructureTemplateAccessor) template).blib$getPalettes();
        if (palettes == null || palettes.isEmpty()) {
            return;
        }

        var palette = palettes.get(0);
        var blocks = palette.blocks();
        if (blocks.isEmpty()) {
            return;
        }

        var maxAxis = Math.max(size.getX(), Math.max(size.getY(), size.getZ()));
        var targetPx = THUMBNAIL_PIXEL_SIZE * 0.78f;
        var scale = targetPx / (float) maxAxis;

        var pose = new PoseStack();
        pose.translate(THUMBNAIL_PIXEL_SIZE / 2.0, THUMBNAIL_PIXEL_SIZE / 2.0, 100.0);
        pose.scale(scale, -scale, scale);
        pose.mulPose(Axis.XP.rotationDegrees(CARD_PERSPECTIVE_PITCH));
        pose.mulPose(Axis.YP.rotationDegrees(CARD_PERSPECTIVE_YAW));
        pose.translate(-size.getX() / 2.0f, -size.getY() / 2.0f, -size.getZ() / 2.0f);

        Lighting.setupFor3DItems();
        RenderSystem.enableDepthTest();

        var mc = Minecraft.getInstance();
        var bufferSource = mc.renderBuffers().bufferSource();
        var blockRenderer = mc.getBlockRenderer();

        for (var info : blocks) {
            var state = info.state();
            if (state.isAir() || state.is(Blocks.STRUCTURE_VOID)) {
                continue;
            }
            var bp = info.pos();
            pose.pushPose();
            pose.translate(bp.getX(), bp.getY(), bp.getZ());
            blockRenderer.renderSingleBlock(state, pose, bufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            pose.popPose();
        }
        bufferSource.endBatch();
    }

    /**
     * Direct framebuffer-to-framebuffer copy from {@code target} into the main RT at the GUI rect's pixel coords.
     * Mirrors {@link com.blib.engine.ui.EngineWorkspaceCompositor}'s pattern for downsampling the world view. Pending
     * GuiGraphics draws are flushed first so the GuiGraphics buffer state is settled when the blit lands.
     */
    private static void blitTargetIntoGui(GuiGraphics graphics, TextureTarget target, int x, int y, int width, int height) {
        graphics.flush();

        var mc = Minecraft.getInstance();
        var mainRT = mc.getMainRenderTarget();
        var fbHeight = mainRT.viewHeight;

        // Walk the GUI rect through the current pose to get screen-logical coords (the workspace's outer SCALE pose
        // is included), then to raw pixels. The framebuffer's Y axis is bottom-origin, so flip Y for the destination.
        var matrix = graphics.pose().last().pose();
        var tl = matrix.transformPosition(x, y, 0, new Vector3f());
        var br = matrix.transformPosition(x + width, y + height, 0, new Vector3f());
        var guiScale = mc.getWindow().getGuiScale();

        var dstX0 = (int) Math.round(tl.x * guiScale);
        var dstX1 = (int) Math.round(br.x * guiScale);
        var dstY0 = (int) Math.round(fbHeight - br.y * guiScale);
        var dstY1 = (int) Math.round(fbHeight - tl.y * guiScale);

        GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, target.frameBufferId);
        GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, mainRT.frameBufferId);
        GlStateManager._glBlitFrameBuffer(
            0,
            0,
            target.viewWidth,
            target.viewHeight,
            dstX0,
            dstY0,
            dstX1,
            dstY1,
            GL30.GL_COLOR_BUFFER_BIT,
            GL30.GL_LINEAR
        );

        // Restore main RT as the active read+draw target so subsequent GuiGraphics submissions and any other
        // glBlitFrameBuffer-using code (the world compositor, other panels) start from a known state.
        mainRT.bindWrite(true);
    }

    /** Solid-fill block when a card hasn't been rendered yet because the per-frame budget was exhausted. */
    private static void drawPlaceholder(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, 0xFF1A1A22);
    }
}
