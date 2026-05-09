package com.blib.engine.jigsaw;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.ApiStatus;

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

        // Translucent-ish ghost: enable blend so the existing world bleeds through, but don't disable depth-test —
        // the preview should occlude correctly against terrain in front of the anchor block.
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        if (collisionCount > 0) {
            RenderSystem.setShaderColor(COLLISION_TINT_R, COLLISION_TINT_G, COLLISION_TINT_B, 1.0f);
        }

        var anchor = placement.anchor();
        poseStack.pushPose();
        poseStack.translate(anchor.getX() - cameraX, anchor.getY() - cameraY, anchor.getZ() - cameraZ);
        try {
            JigsawPiecePreview.renderInWorld(template, poseStack, bufferSource, placement.rotation(), placement.mirror());
            bufferSource.endBatch();
        } finally {
            poseStack.popPose();
            // Reset shader color regardless of which branch we took, so other renderers (selection box, debug
            // overlays) downstream see vanilla white.
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.disableBlend();
        }
    }
}
