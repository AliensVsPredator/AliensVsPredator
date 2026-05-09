package com.blib.engine.jigsaw;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
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
     * Render the structure's blocks into the current pose with the given rotation and mirror applied. Caller is
     * responsible for translating to the desired world position (relative to the camera) before calling.
     * <p>
     * Per-block transforms mirror what {@link StructureTemplate#placeInWorld} does internally:
     * {@link StructureTemplate#transform} computes each block's transformed local position around the
     * {@link BlockPos#ZERO} pivot (matching the placement packet, which sends {@code anchor} as both {@code offset} and
     * {@code pos} to {@code placeInWorld} — and the default {@link net.minecraft.world.level.levelgen.structure
     * .templatesystem.StructurePlaceSettings#getRotationPivot} is {@code BlockPos.ZERO}). The block state itself is
     * mirrored then rotated so blocks with directional appearance (stairs, logs, etc.) face the right way.
     * <p>
     * Air / structure-void cells are skipped.
     */
    public static void renderInWorld(
        StructureTemplate template,
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        Rotation rotation,
        Mirror mirror
    ) {
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
        var pivot = BlockPos.ZERO;

        for (var info : blocks) {
            var state = info.state();
            if (state.isAir() || state.is(Blocks.STRUCTURE_VOID)) {
                continue;
            }

            var transformedPos = StructureTemplate.transform(info.pos(), mirror, rotation, pivot);
            var transformedState = state.mirror(mirror).rotate(rotation);

            poseStack.pushPose();
            poseStack.translate(transformedPos.getX(), transformedPos.getY(), transformedPos.getZ());
            blockRenderer.renderSingleBlock(
                transformedState,
                poseStack,
                bufferSource,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY
            );
            poseStack.popPose();
        }
    }
}
