package com.blib.engine.render.volume;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

import com.blib.engine.domain.selection.volume.BlockSelection;
import com.blib.engine.session.EngineMode;
import com.blib.internal.client.shader.BLibShaders;

/**
 * Renders the destination preview during a Move Blocks drag — a translucent green AABB at {@code originAABB + offset}.
 * Active only when (1) MOVE_BLOCKS is the current tool, (2) a non-zero {@link BlockSelection#moveOffset} is set, and
 * (3) the engine workspace is active. The source AABB continues to render via {@link BlockSelectionWireframeRenderer}
 * (yellow/cyan based on capture mode), so the user sees both "from" and "to" volumes simultaneously — Paint.NET's
 * marquee-with-floating-selection pattern.
 * <p>
 * v1 renders only the destination AABB outline (no per-block ghost). The {@code 256³} volume cap means per-block
 * rendering would be ~16M extra blocks worst-case — well outside live-preview budget. A future iteration could add
 * per-block rendering for small AABBs (&lt; ~8K blocks) without changing the public surface.
 */
@ApiStatus.Internal
public final class MoveBlocksGhostRenderer {

    /** Destination preview color (green) — distinct from the source AABB's yellow/cyan so "to" is unambiguous. */
    private static final float DEST_R = 0.30f;

    private static final float DEST_G = 0.95f;

    private static final float DEST_B = 0.30f;

    /** Volume alpha — matches the source-AABB volume alpha so neither dominates visually. */
    private static final float VOLUME_ALPHA = 0.18f;

    /** Slight inflate on the AABB to avoid z-fighting with the destination's existing block faces. */
    private static final double AABB_INFLATE = 0.04;

    private MoveBlocksGhostRenderer() {}

    public static void render(PoseStack poseStack, double cameraX, double cameraY, double cameraZ) {
        if (!EngineMode.get().isActive()) {
            return;
        }
        if (BlockSelection.gizmoMode() != BlockSelection.GizmoMode.MOVE_BLOCKS) {
            return;
        }
        var offset = BlockSelection.moveOffset();
        if (offset == null) {
            return;
        }
        if (offset.getX() == 0 && offset.getY() == 0 && offset.getZ() == 0) {
            return;
        }
        var aabb = BlockSelection.aabb();
        if (aabb.isEmpty()) {
            return;
        }
        if (BLibShaders.ENGINE_SELECTION.instance() == null) {
            return;
        }

        var box = aabb.get();
        var matrix = poseStack.last().pose();

        RenderSystem.setShader(BLibShaders.ENGINE_SELECTION.supplier());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        var minX = (float) (box.minX + offset.getX() - cameraX - AABB_INFLATE);
        var minY = (float) (box.minY + offset.getY() - cameraY - AABB_INFLATE);
        var minZ = (float) (box.minZ + offset.getZ() - cameraZ - AABB_INFLATE);
        var maxX = (float) (box.maxX + offset.getX() - cameraX + AABB_INFLATE);
        var maxY = (float) (box.maxY + offset.getY() - cameraY + AABB_INFLATE);
        var maxZ = (float) (box.maxZ + offset.getZ() - cameraZ + AABB_INFLATE);
        addBoxQuads(buffer, matrix, minX, minY, minZ, maxX, maxY, maxZ, DEST_R, DEST_G, DEST_B, VOLUME_ALPHA);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
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
