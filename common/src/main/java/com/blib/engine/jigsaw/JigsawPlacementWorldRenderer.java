package com.blib.engine.jigsaw;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

import com.blib.engine.jigsaw.placement.CollisionScanner;
import com.blib.engine.jigsaw.placement.JigsawBlockTarget;
import com.blib.engine.jigsaw.placement.JigsawPlacementFrameState;
import com.blib.engine.jigsaw.placement.JigsawTool;
import com.blib.engine.jigsaw.placement.JigsawWorldRaycast;
import com.blib.engine.jigsaw.placement.PlacementContext;
import com.blib.engine.jigsaw.placement.PlacementMode;
import com.blib.engine.session.EngineMode;

/**
 * Per-frame hook that ghosts the user's currently-selected jigsaw piece into the world at the position chosen by the
 * active {@link com.blib.engine.jigsaw.placement.PlacementResolver}. Plugged into the existing debug-render injection
 * so the geometry is camera-relative and lit consistently with the rest of the world. No-ops fast when engine mode is
 * off, no piece is selected, or the resolver returns {@code null} ("no valid placement at this cursor position").
 * <p>
 * Mesh data is bake-once / draw-many via {@link JigsawPreviewMeshCache}: the per-block model walk only runs the first
 * time the user holds a particular (template, rotation, mirror) tuple; subsequent frames bind the cached
 * {@link com.mojang.blaze3d.vertex.VertexBuffer}s and submit one draw per RenderType regardless of piece size.
 */
@ApiStatus.Internal
public final class JigsawPlacementWorldRenderer {

    /**
     * Color modulator applied to the ghost when collisions are detected. RGB pulled toward red so existing-block
     * overlaps read clearly even on light templates; alpha left at 1 because chunk render types ignore alpha for solid
     * blocks (keeping it at 1 also avoids needing depth-test gymnastics for the tinted variant).
     */
    private static final float COLLISION_TINT_R = 1.00f;

    private static final float COLLISION_TINT_G = 0.45f;

    private static final float COLLISION_TINT_B = 0.45f;

    private JigsawPlacementWorldRenderer() {}

    /**
     * Render the ghost preview. The {@code poseStack} and {@code bufferSource} parameters are kept for the existing
     * mixin call shape but are unused now — the cached path manages its own model-view matrix via
     * {@link RenderSystem#getModelViewMatrix()} and submits draws directly to the bound
     * {@link com.mojang.blaze3d.vertex.VertexBuffer}, bypassing the shared chunk buffer source.
     */
    public static void render(
        PoseStack poseStack,
        MultiBufferSource.BufferSource bufferSource,
        double cameraX,
        double cameraY,
        double cameraZ
    ) {
        // Always start by clearing frame state — if any of the early-bail conditions fire, downstream consumers
        // (status bar, click handler) should see "no placement this frame" rather than stale data from the last
        // valid resolve.
        JigsawPlacementFrameState.clear();

        var session = EngineMode.get().session();
        if (session == null) {
            return;
        }

        var selectedId = JigsawPieceSelection.selectedId();
        if (selectedId == null) {
            return;
        }

        var template = JigsawPieceLibrary.get(selectedId);
        if (template == null) {
            return;
        }

        var ctx = new PlacementContext(session, template, JigsawPieceSelection.rotation(), JigsawPieceSelection.mirror());
        var placement = JigsawTool.activeResolver().resolve(ctx);
        if (placement == null) {
            return;
        }

        // Re-raycast for the snap anchor so the collision scanner can exempt the connection block. The resolver
        // already raycasted internally, but exposing it from the resolver vs re-doing the (cheap) clip call here
        // would couple call sites — three raycasts per frame for the same block is fine in practice.
        JigsawBlockTarget snapAnchor = null;
        if (JigsawTool.activeMode() == PlacementMode.JIGSAW_SNAP) {
            snapAnchor = JigsawWorldRaycast.raycastJigsaw(session);
        }

        var mc = Minecraft.getInstance();
        var collisionCount = mc.level == null ? 0 : CollisionScanner.scan(mc.level, placement, template, snapAnchor);
        JigsawPlacementFrameState.update(placement, snapAnchor, collisionCount);

        var baked = JigsawPreviewMeshCache.getOrBuild(template, placement.rotation(), placement.mirror());
        if (baked.isEmpty()) {
            return;
        }

        // Build the per-frame model-view: camera rotation (already on RenderSystem during debug-render injection) ×
        // translation from camera to placement anchor. The cached vertices are in template-local coords, so this
        // matrix is what positions them at the right spot in view space.
        var anchor = placement.anchor();
        var modelView = new Matrix4f(RenderSystem.getModelViewMatrix());
        modelView.translate(
            (float) (anchor.getX() - cameraX),
            (float) (anchor.getY() - cameraY),
            (float) (anchor.getZ() - cameraZ)
        );
        var projection = RenderSystem.getProjectionMatrix();

        if (collisionCount > 0) {
            RenderSystem.setShaderColor(COLLISION_TINT_R, COLLISION_TINT_G, COLLISION_TINT_B, 1.0f);
        }

        try {
            baked.render(modelView, projection);

            // Block-entity pass: chests, signs, banners, skulls, beds — vanilla doesn't bake their visible geometry
            // into the chunk mesh (their RenderShape is ENTITYBLOCK_ANIMATED), so they need a separate BE-renderer
            // dispatch on top of the cached mesh draw. Goes through the shared bufferSource because BE renderers
            // submit their vertices that way; the endBatch flushes them as a single draw per render type.
            poseStack.pushPose();
            poseStack.translate(anchor.getX() - cameraX, anchor.getY() - cameraY, anchor.getZ() - cameraZ);
            baked.renderBlockEntities(poseStack, bufferSource, 0.0f);
            poseStack.popPose();
            bufferSource.endBatch();
        } finally {
            // Reset shader color regardless of which branch we took, so other renderers (selection box, debug
            // overlays) downstream see vanilla white.
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }
}
