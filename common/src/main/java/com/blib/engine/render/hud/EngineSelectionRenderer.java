package com.blib.engine.render.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

import com.blib.engine.domain.selection.picking.SelectionManager;
import com.blib.engine.session.EngineMode;
import com.blib.internal.client.shader.BLibShaders;

/**
 * Draws the engine-mode selection visual using BLib's custom {@code engine_selection} shader. Renders the selected
 * entity's bounding box as a translucent volume with depth-test disabled, so the selection reads through walls / other
 * entities. The shader does the time-pulse effect; this class just submits geometry + colour.
 */
@ApiStatus.Internal
public final class EngineSelectionRenderer {

    private static final float COLOR_R = 1.00f;

    private static final float COLOR_G = 0.55f;

    private static final float COLOR_B = 0.05f;

    private static final float COLOR_A = 0.40f;

    /** Padding (in blocks) added to the entity AABB so the volume doesn't z-fight the body. */
    private static final double AABB_INFLATE = 0.04;

    private EngineSelectionRenderer() {}

    public static void render(
        PoseStack poseStack,
        MultiBufferSource.BufferSource bufferSource,
        double cameraX,
        double cameraY,
        double cameraZ
    ) {
        // Engine-mode gate matches the other engine renderers — outside the workspace the selection state may linger
        // (the workspace's removed() clears it in IN_GAME, but the user has B-toggled away by then and shouldn't see
        // engine UI bleeding into the live game).
        if (!EngineMode.get().isActive()) {
            return;
        }
        var selection = SelectionManager.current();
        if (selection.isEmpty()) {
            return;
        }

        var shader = BLibShaders.ENGINE_SELECTION.instance();
        // Shader can be null briefly during initial resource load or after F3+T reload — bail rather than NPE.
        if (shader == null) {
            return;
        }

        var matrix = poseStack.last().pose();

        // GL state: alpha-blended, depth-test off (visible through walls), default color modulator. Restore the
        // bits we touched at the end so subsequent debug renderers see vanilla defaults. Setup runs once and is
        // shared across every selectable's box draw.
        RenderSystem.setShader(BLibShaders.ENGINE_SELECTION.supplier());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        // One draw call per selectable. Multi-select (deferred from phase-7 MVP) will exercise this loop with more
        // than one item; today it's effectively a single iteration.
        for (var item : selection.items()) {
            var aabb = item.worldBounds().inflate(AABB_INFLATE);
            var minX = (float) (aabb.minX - cameraX);
            var minY = (float) (aabb.minY - cameraY);
            var minZ = (float) (aabb.minZ - cameraZ);
            var maxX = (float) (aabb.maxX - cameraX);
            var maxY = (float) (aabb.maxY - cameraY);
            var maxZ = (float) (aabb.maxZ - cameraZ);

            var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            addBoxQuads(buffer, matrix, minX, minY, minZ, maxX, maxY, maxZ, COLOR_R, COLOR_G, COLOR_B, COLOR_A);
            BufferUploader.drawWithShader(buffer.buildOrThrow());
        }

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
        // Six faces, four vertices each, wound counter-clockwise when viewed from outside the box. With cull on,
        // backfaces (interior faces when the camera is outside) get skipped.
        // -Z (north face)
        buffer.addVertex(matrix, maxX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, maxY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, maxY, minZ).setColor(r, g, b, a);
        // +Z (south face)
        buffer.addVertex(matrix, minX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, maxY, maxZ).setColor(r, g, b, a);
        // -X (west face)
        buffer.addVertex(matrix, minX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, maxY, minZ).setColor(r, g, b, a);
        // +X (east face)
        buffer.addVertex(matrix, maxX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, maxY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, maxY, maxZ).setColor(r, g, b, a);
        // -Y (bottom face)
        buffer.addVertex(matrix, minX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, minY, maxZ).setColor(r, g, b, a);
        // +Y (top face)
        buffer.addVertex(matrix, minX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, maxX, maxY, minZ).setColor(r, g, b, a);
        buffer.addVertex(matrix, minX, maxY, minZ).setColor(r, g, b, a);
    }
}
