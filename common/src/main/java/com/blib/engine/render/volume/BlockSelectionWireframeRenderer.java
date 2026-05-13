package com.blib.engine.render.volume;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

import com.blib.engine.domain.selection.picking.BlockSelectable;
import com.blib.engine.domain.selection.picking.EntitySelectable;
import com.blib.engine.domain.selection.picking.SelectionManager;
import com.blib.engine.domain.selection.volume.BlockSelection;
import com.blib.engine.session.EngineMode;
import com.blib.internal.client.shader.BLibShaders;
import com.blib.internal.common.capture.CaptureMode;

/**
 * Per-frame world-render hook that highlights the user's current capture-volume selection: a translucent volume
 * spanning the AABB defined by the two corner blocks, plus a brighter highlight on each individually picked corner (so
 * the user can tell which corners are set when only one has been clicked so far).
 * <p>
 * Color depends on {@link CaptureMode}: {@code GENERAL} captures use a warm yellow, {@code JIGSAW} captures use a cool
 * cyan to match the seam-block aesthetic. Both are low-alpha so the user can see the captured blocks through the
 * overlay; an alpha bump on the corner markers makes them legible against any background.
 * <p>
 * Plugged into the same debug-render injection as {@link com.blib.engine.render.jigsaw.JigsawPlacementWorldRenderer}.
 * No-ops fast when engine mode is off or the selection has no corners.
 */
@ApiStatus.Internal
public final class BlockSelectionWireframeRenderer {

    /** Yellow for {@link CaptureMode#GENERAL}. */
    private static final float GENERAL_R = 1.00f;

    private static final float GENERAL_G = 0.85f;

    private static final float GENERAL_B = 0.20f;

    /** Cyan for {@link CaptureMode#JIGSAW}. */
    private static final float JIGSAW_R = 0.20f;

    private static final float JIGSAW_G = 0.85f;

    private static final float JIGSAW_B = 1.00f;

    /** Volume alpha. Low so the user can see the captured blocks through the overlay. */
    private static final float VOLUME_ALPHA = 0.18f;

    /** Corner-marker alpha. Higher so individual block picks read clearly. */
    private static final float CORNER_ALPHA = 0.55f;

    /** Padding (in blocks) around the marker AABB to avoid z-fighting against the block face. */
    private static final double AABB_INFLATE = 0.04;

    private BlockSelectionWireframeRenderer() {}

    public static void render(PoseStack poseStack, double cameraX, double cameraY, double cameraZ) {
        // Engine-mode gate: outside the workspace, the capture state may be stale and shouldn't paint over the world.
        if (!EngineMode.get().isActive()) {
            return;
        }
        // Entity selection takes over the gizmo surface — hide the volume wireframe so it doesn't visually compete with
        // the entity's selection highlight.
        var single = SelectionManager.current().single();
        if (single instanceof EntitySelectable) {
            return;
        }

        // Single-block selection (generic block or jigsaw): draw a 1×1×1 highlight at the block and skip the volume
        // box. Conveys "this block is selected" with the same visual vocabulary as the corner markers on a multi-
        // block volume — same color, same translucent shading — so users don't need to learn a second affordance.
        if (single instanceof BlockSelectable bs) {
            renderSingleBlockHighlight(bs.pos(), poseStack, cameraX, cameraY, cameraZ);
            return;
        }

        var a = BlockSelection.cornerA();
        var b = BlockSelection.cornerB();
        if (a == null && b == null) {
            return;
        }

        var shader = BLibShaders.ENGINE_SELECTION.instance();
        if (shader == null) {
            return;
        }

        var matrix = poseStack.last().pose();
        var color = colorFor(BlockSelection.mode());

        RenderSystem.setShader(BLibShaders.ENGINE_SELECTION.supplier());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        // Depth-test off so the volume is visible through occluders — without this, indoors captures would only show
        // the parts of the volume on the camera-facing side of nearby walls, which is confusing.
        RenderSystem.disableDepthTest();

        var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        if (a != null && b != null) {
            // Full AABB volume.
            var minX = (float) (Math.min(a.getX(), b.getX()) - cameraX - AABB_INFLATE);
            var minY = (float) (Math.min(a.getY(), b.getY()) - cameraY - AABB_INFLATE);
            var minZ = (float) (Math.min(a.getZ(), b.getZ()) - cameraZ - AABB_INFLATE);
            var maxX = (float) (Math.max(a.getX(), b.getX()) + 1 - cameraX + AABB_INFLATE);
            var maxY = (float) (Math.max(a.getY(), b.getY()) + 1 - cameraY + AABB_INFLATE);
            var maxZ = (float) (Math.max(a.getZ(), b.getZ()) + 1 - cameraZ + AABB_INFLATE);
            addBoxQuads(buffer, matrix, minX, minY, minZ, maxX, maxY, maxZ, color[0], color[1], color[2], VOLUME_ALPHA);
        }
        // Corner highlights on top — drawn even when both corners are set, just to give the user feedback on which
        // cells they actually clicked. Brighter alpha + slight extra inflate so the markers read above the volume.
        if (a != null) {
            addBoxQuadsForBlock(buffer, matrix, a, cameraX, cameraY, cameraZ, color[0], color[1], color[2], CORNER_ALPHA);
        }
        if (b != null) {
            addBoxQuadsForBlock(buffer, matrix, b, cameraX, cameraY, cameraZ, color[0], color[1], color[2], CORNER_ALPHA);
        }
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    /**
     * Draw one translucent block-sized highlight at {@code pos}. Used for the single-block inspect affordance — the
     * generic block / jigsaw inspector both surface this so the user sees what they've picked in the world even when
     * the inspector panel is offscreen or scrolled away.
     */
    private static void renderSingleBlockHighlight(
        BlockPos pos,
        PoseStack poseStack,
        double cameraX,
        double cameraY,
        double cameraZ
    ) {
        var shader = BLibShaders.ENGINE_SELECTION.instance();
        if (shader == null) {
            return;
        }

        var matrix = poseStack.last().pose();
        var color = colorFor(BlockSelection.mode());

        RenderSystem.setShader(BLibShaders.ENGINE_SELECTION.supplier());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        // Depth-test off to match the volume-wireframe convention — the highlight remains visible through occluders.
        RenderSystem.disableDepthTest();

        var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        addBoxQuadsForBlock(buffer, matrix, pos, cameraX, cameraY, cameraZ, color[0], color[1], color[2], CORNER_ALPHA);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private static float[] colorFor(CaptureMode mode) {
        return switch (mode) {
            case JIGSAW -> new float[] { JIGSAW_R, JIGSAW_G, JIGSAW_B };
            case GENERAL -> new float[] { GENERAL_R, GENERAL_G, GENERAL_B };
        };
    }

    private static void addBoxQuadsForBlock(
        BufferBuilder buffer,
        Matrix4f matrix,
        BlockPos pos,
        double cameraX,
        double cameraY,
        double cameraZ,
        float r,
        float g,
        float b,
        float a
    ) {
        var inflate = AABB_INFLATE * 2.0; // marker is bigger than the volume's edge inflate so it reads above
        var minX = (float) (pos.getX() - cameraX - inflate);
        var minY = (float) (pos.getY() - cameraY - inflate);
        var minZ = (float) (pos.getZ() - cameraZ - inflate);
        var maxX = (float) (pos.getX() + 1 - cameraX + inflate);
        var maxY = (float) (pos.getY() + 1 - cameraY + inflate);
        var maxZ = (float) (pos.getZ() + 1 - cameraZ + inflate);
        addBoxQuads(buffer, matrix, minX, minY, minZ, maxX, maxY, maxZ, r, g, b, a);
    }

    private static void addBoxQuads(
        BufferBuilder buffer,
        Matrix4f matrix,
        float minX,
        float minY,
        float minZ,
        float maxX,
        float maxY,
        float maxZ,
        float r,
        float g,
        float b,
        float a
    ) {
        // -Z
        buffer.addVertex(matrix, maxX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, maxY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, maxY, minZ).setColor(r, g, b, a);
        // +Z
        buffer.addVertex(matrix, minX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, maxY, maxZ).setColor(r, g, b, a);
        // -X
        buffer.addVertex(matrix, minX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, maxY, minZ).setColor(r, g, b, a);
        // +X
        buffer.addVertex(matrix, maxX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, maxY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, maxY, maxZ).setColor(r, g, b, a);
        // -Y
        buffer.addVertex(matrix, minX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, minY, maxZ).setColor(r, g, b, a);
        // +Y
        buffer.addVertex(matrix, minX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, maxY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, maxY, minZ).setColor(r, g, b, a);
    }
}
