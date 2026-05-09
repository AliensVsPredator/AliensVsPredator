package com.blib.engine.jigsaw.placement;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.jigsaw.JigsawPlacementCursor;

/**
 * Free-form placement: anchor is positioned so the structure's footprint is horizontally centered on the cursor's
 * targeted block and its bottom rests at that block's Y. Rotation and mirror pass through from the user's selection
 * unchanged.
 * <p>
 * The translation between "cursor world position" and "anchor passed to {@code placeInWorld}" is non-trivial because
 * {@code placeInWorld} positions the template's local {@code (0,0,0)} at the anchor, not the structure's center — left
 * raw, the user sees the structure's corner snap to the cursor and the geometry trail off into +X/+Z. Computing the
 * rotated bounding box and offsetting the anchor by its center fixes that without changing what the server receives
 * (still {@code placeInWorld(level, anchor, anchor, settings, ...)}).
 * <p>
 * Returns {@code null} when the cursor doesn't hit anything (open sky / void), which suppresses the ghost preview and
 * ignores LMB clicks until the cursor finds a surface.
 */
@ApiStatus.Internal
public final class FreePlacementResolver implements PlacementResolver {

    public static final FreePlacementResolver INSTANCE = new FreePlacementResolver();

    private FreePlacementResolver() {}

    @Override
    public @Nullable Placement resolve(PlacementContext ctx) {
        var cursorAnchor = JigsawPlacementCursor.resolveAnchorBlock(ctx.session());
        if (cursorAnchor == null) {
            return null;
        }

        // Compute the rotated bbox the structure would occupy if anchored at (0,0,0). This handles rotation/mirror
        // correctly — for CLOCKWISE_90 the bbox's minX is negative, etc. — without us having to re-derive the
        // transform math. We then offset the cursor by the bbox's XZ-center and Y-min so:
        // - structure horizontal center == cursorAnchor (centered on the cursor)
        // - structure bottom Y == cursorAnchor.y (bottom rests on the surface block above the hit face)
        var settings = new StructurePlaceSettings().setRotation(ctx.userRotation()).setMirror(ctx.userMirror());
        var baseBbox = ctx.template().getBoundingBox(settings, BlockPos.ZERO);
        var centerX = (baseBbox.minX() + baseBbox.maxX()) / 2;
        var centerZ = (baseBbox.minZ() + baseBbox.maxZ()) / 2;
        var minY = baseBbox.minY();

        var anchor = new BlockPos(
            cursorAnchor.getX() - centerX,
            cursorAnchor.getY() - minY,
            cursorAnchor.getZ() - centerZ
        );
        return new Placement(anchor, ctx.userRotation(), ctx.userMirror());
    }
}
