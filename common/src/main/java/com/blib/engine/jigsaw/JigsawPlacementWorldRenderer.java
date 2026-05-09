package com.blib.engine.jigsaw;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.session.EngineMode;

/**
 * Per-frame hook that ghosts the user's currently-selected jigsaw piece into the world at the cursor's targeted block.
 * Plugged into the existing debug-render injection so the geometry is camera-relative and lit consistently with the
 * rest of the world. No-ops fast when engine mode is off, no piece is selected, or the cursor isn't over the viewport.
 */
@ApiStatus.Internal
public final class JigsawPlacementWorldRenderer {

    private JigsawPlacementWorldRenderer() {}

    public static void render(
        PoseStack poseStack,
        MultiBufferSource.BufferSource bufferSource,
        double cameraX,
        double cameraY,
        double cameraZ
    ) {
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

        var anchor = JigsawPlacementCursor.resolveAnchorBlock(session);
        if (anchor == null) {
            return;
        }

        // Translucent-ish ghost: enable blend so the existing world bleeds through, but don't disable depth-test —
        // the preview should occlude correctly against terrain in front of the anchor block.
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        poseStack.pushPose();
        poseStack.translate(anchor.getX() - cameraX, anchor.getY() - cameraY, anchor.getZ() - cameraZ);
        try {
            JigsawPiecePreview.renderInWorld(template, poseStack, bufferSource);
            bufferSource.endBatch();
        } finally {
            poseStack.popPose();
            RenderSystem.disableBlend();
        }
    }
}
