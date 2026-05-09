package com.blib.engine.jigsaw;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.worldgen.v1.StructureTemplateAccessor;

/**
 * World-space rendering helper that draws the blocks of a {@link StructureTemplate} into a caller-supplied pose. Used
 * by the placement preview to ghost the structure where it would be placed; panel cards go through
 * {@link JigsawPieceThumbnailCache} instead, which renders each preview into a {@code RenderTarget} once and blits the
 * texture every frame thereafter.
 */
@ApiStatus.Internal
public final class JigsawPiecePreview {

    private JigsawPiecePreview() {}

    /**
     * Render the structure's blocks into the current pose. Caller is responsible for translating to the desired world
     * position (relative to the camera). Air / structure-void cells are skipped.
     */
    public static void renderInWorld(StructureTemplate template, PoseStack poseStack, MultiBufferSource bufferSource) {
        var palettes = ((StructureTemplateAccessor) template).blib$getPalettes();
        if (palettes == null || palettes.isEmpty()) {
            return;
        }

        // Templates can declare multiple palettes for variation; placeInWorld picks one at random per call. For
        // previews we always use the first so what the user sees matches what gets placed when they confirm.
        var palette = palettes.get(0);
        var blocks = palette.blocks();
        if (blocks.isEmpty()) {
            return;
        }

        var blockRenderer = Minecraft.getInstance().getBlockRenderer();

        for (var info : blocks) {
            var state = info.state();
            if (state.isAir() || state.is(Blocks.STRUCTURE_VOID)) {
                continue;
            }

            var pos = info.pos();
            poseStack.pushPose();
            poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
            blockRenderer.renderSingleBlock(
                state,
                poseStack,
                bufferSource,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY
            );
            poseStack.popPose();
        }
    }
}
