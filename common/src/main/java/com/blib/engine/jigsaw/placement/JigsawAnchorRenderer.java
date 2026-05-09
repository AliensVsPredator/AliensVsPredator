package com.blib.engine.jigsaw.placement;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

import com.blib.engine.jigsaw.JigsawPieceLibrary;
import com.blib.engine.jigsaw.JigsawPieceSelection;
import com.blib.engine.session.EngineMode;
import com.blib.internal.client.shader.BLibShaders;

/**
 * World-render hook that highlights the jigsaw block the {@link JigsawSnapResolver} is currently snapping to. Drawn as
 * a translucent volume around the anchor block (slightly inflated so it doesn't z-fight) using the existing
 * {@code engine_selection} shader, matching the style of {@link com.blib.engine.hud.EngineSelectionRenderer} for entity
 * selection.
 * <p>
 * Re-runs the raycast each frame rather than reading state cached by the resolver — both calls hit the same
 * {@link JigsawWorldRaycast} helper which is cheap, and decoupling avoids the resolver having to expose its
 * intermediate hit result for the render-only consumer.
 */
@ApiStatus.Internal
public final class JigsawAnchorRenderer {

    /** Saturated cyan, distinct from the entity-selection orange so the user can tell at a glance which is which. */
    private static final float COLOR_R = 0.10f;

    private static final float COLOR_G = 0.85f;

    private static final float COLOR_B = 1.00f;

    private static final float COLOR_A = 0.45f;

    /** Padding (in blocks) added to the anchor AABB so the volume doesn't z-fight the jigsaw block face. */
    private static final double AABB_INFLATE = 0.04;

    private JigsawAnchorRenderer() {}

    public static void render(PoseStack poseStack, double cameraX, double cameraY, double cameraZ) {
        // Only render when the user is actively in JIGSAW_SNAP mode and has a piece selected — outside that, the
        // anchor highlight is just visual noise.
        if (JigsawTool.activeMode() != PlacementMode.JIGSAW_SNAP) {
            return;
        }
        if (!JigsawPieceSelection.hasSelection()) {
            return;
        }

        var session = EngineMode.get().session();
        if (session == null) {
            return;
        }
        // Bail if the active piece can't even be loaded — without a template there's no resolver run, and we don't
        // want to highlight a block the user couldn't actually snap to.
        var selectedId = JigsawPieceSelection.selectedId();
        if (selectedId == null || JigsawPieceLibrary.get(selectedId) == null) {
            return;
        }

        var anchor = JigsawWorldRaycast.raycastJigsaw(session);
        if (anchor == null) {
            return;
        }

        var shader = BLibShaders.ENGINE_SELECTION.instance();
        if (shader == null) {
            return;
        }

        var pos = anchor.worldPos();
        var minX = (float) (pos.getX() - cameraX - AABB_INFLATE);
        var minY = (float) (pos.getY() - cameraY - AABB_INFLATE);
        var minZ = (float) (pos.getZ() - cameraZ - AABB_INFLATE);
        var maxX = (float) (pos.getX() + 1 - cameraX + AABB_INFLATE);
        var maxY = (float) (pos.getY() + 1 - cameraY + AABB_INFLATE);
        var maxZ = (float) (pos.getZ() + 1 - cameraZ + AABB_INFLATE);

        var matrix = poseStack.last().pose();
        RenderSystem.setShader(BLibShaders.ENGINE_SELECTION.supplier());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        // depth-test off so the highlight reads through the structure ghost being rendered on top of the anchor —
        // otherwise the ghost would occlude the highlight from most camera angles and it'd flicker on/off as the
        // user orbits.
        RenderSystem.disableDepthTest();

        var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        addBoxQuads(buffer, matrix, minX, minY, minZ, maxX, maxY, maxZ);
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
        float maxZ
    ) {
        var r = COLOR_R;
        var g = COLOR_G;
        var b = COLOR_B;
        var a = COLOR_A;
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
