package com.blib.engine.jigsaw.placement;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.jigsaw.JigsawPlacementCursor;

/**
 * Grid-snap resolver: rounds the cursor anchor to the nearest multiple of {@link JigsawPlacementOptions#gridSize} on
 * each axis before applying the standard XZ-centering. Default grid size 16 = chunk-aligned, which is the common
 * authoring intent.
 */
@ApiStatus.Internal
public final class GridSnapResolver implements PlacementResolver {

    public static final GridSnapResolver INSTANCE = new GridSnapResolver();

    private GridSnapResolver() {}

    @Override
    public @Nullable Placement resolve(PlacementContext ctx) {
        var cursorAnchor = JigsawPlacementCursor.resolveAnchorBlock(ctx.session());
        if (cursorAnchor == null) {
            return null;
        }

        var gridSize = JigsawPlacementOptions.gridSize();

        var anchorX = cursorAnchor.getX();
        var anchorY = cursorAnchor.getY();
        var anchorZ = cursorAnchor.getZ();
        if (gridSize > 1) {
            anchorX = roundToMultiple(anchorX, gridSize);
            anchorY = roundToMultiple(anchorY, gridSize);
            anchorZ = roundToMultiple(anchorZ, gridSize);
        }

        // Same centering math as FREE so the user's mental model of "structure follows my cursor" stays intact —
        // grid snap just changes which integer cells the cursor itself maps to.
        var settings = new StructurePlaceSettings().setRotation(ctx.userRotation()).setMirror(ctx.userMirror());
        var baseBbox = ctx.template().getBoundingBox(settings, BlockPos.ZERO);
        var centerX = (baseBbox.minX() + baseBbox.maxX()) / 2;
        var centerZ = (baseBbox.minZ() + baseBbox.maxZ()) / 2;
        var minY = baseBbox.minY();

        var anchor = new BlockPos(anchorX - centerX, anchorY - minY, anchorZ - centerZ);
        return new Placement(anchor, ctx.userRotation(), ctx.userMirror());
    }

    /** Round {@code value} to the nearest multiple of {@code multiple}. Standard half-up rounding via floats. */
    private static int roundToMultiple(int value, int multiple) {
        return Math.round((float) value / multiple) * multiple;
    }
}
