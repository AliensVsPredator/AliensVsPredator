package com.blib.engine.jigsaw.placement;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.api.common.worldgen.v1.StructureTemplateAccessor;

/**
 * Counts the world blocks a {@link Placement} would overwrite. Walks the active palette's block list, transforms each
 * local position by the placement's rotation/mirror (the same math {@code placeInWorld} uses), and queries the level's
 * existing block state at the resulting world position.
 * <p>
 * Iteration is template-bound rather than AABB-bound — for sparse templates (which most jigsaw pieces are) this is
 * strictly fewer block lookups than an AABB walk would be, and {@code STRUCTURE_VOID} is naturally skipped because the
 * template doesn't write at those positions.
 * <p>
 * For {@link PlacementMode#JIGSAW_SNAP}, the resolver places the candidate's jigsaw block at {@code anchor.worldPos
 * + anchor.front} — that single connecting cell is exempt from the count because the face-to-face geometry makes
 * "overlap" there expected. Without the exemption, every snap-placement would count the connection block as a collision
 * and the user couldn't tell the difference between intended and unintended overlaps.
 */
@ApiStatus.Internal
public final class CollisionScanner {

    private CollisionScanner() {}

    /**
     * Returns the number of non-air world blocks the placement would overwrite. {@code snapAnchor} should be the jigsaw
     * block the resolver snapped to in {@link PlacementMode#JIGSAW_SNAP}, or {@code null} for any other mode — the
     * connection-block exemption only fires when an anchor is supplied.
     */
    public static int scan(Level level, Placement placement, StructureTemplate template, @Nullable JigsawBlockTarget snapAnchor) {
        var palettes = ((StructureTemplateAccessor) template).blib$getPalettes();
        if (palettes == null || palettes.isEmpty()) {
            return 0;
        }
        var blocks = palettes.get(0).blocks();
        if (blocks.isEmpty()) {
            return 0;
        }

        var rotation = placement.rotation();
        var mirror = placement.mirror();
        var placementAnchor = placement.anchor();

        BlockPos exemptPos = null;
        if (snapAnchor != null) {
            exemptPos = snapAnchor.worldPos().relative(snapAnchor.front());
        }

        int count = 0;
        for (var info : blocks) {
            var state = info.state();
            // STRUCTURE_VOID never writes — vanilla placeInWorld skips these positions, so we do too. Without this
            // check, intentionally-hollowed templates (e.g. an arch with structure-voided interior) would inflate
            // the collision count with their void cells regardless of what's actually under them.
            if (state.is(Blocks.STRUCTURE_VOID)) {
                continue;
            }

            var localTransformed = StructureTemplate.transform(info.pos(), mirror, rotation, BlockPos.ZERO);
            var worldPos = new BlockPos(
                placementAnchor.getX() + localTransformed.getX(),
                placementAnchor.getY() + localTransformed.getY(),
                placementAnchor.getZ() + localTransformed.getZ()
            );

            if (worldPos.equals(exemptPos)) {
                continue;
            }

            var existingState = level.getBlockState(worldPos);
            if (!existingState.isAir()) {
                count++;
            }
        }
        return count;
    }
}
