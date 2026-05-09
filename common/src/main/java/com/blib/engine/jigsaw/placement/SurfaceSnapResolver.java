package com.blib.engine.jigsaw.placement;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.jigsaw.JigsawPlacementCursor;

/**
 * Surface-snap resolver: drops the structure onto the topmost solid block of the cursor's column, regardless of which
 * face of which block the raycast happens to hit. Useful when the user wants the structure to settle on the ground
 * without worrying about whether they clicked the top of a grass block (FREE works) versus the side of a wall (FREE
 * places the structure floating off the wall side).
 * <p>
 * Algorithm:
 * <ol>
 * <li>Raycast cursor to find the hit block (just for X/Z coords).</li>
 * <li>{@code Heightmap.MOTION_BLOCKING_NO_LEAVES} returns the Y of the lowest air cell above the surface at that column
 * — i.e., the cell where a structure's bottom row should sit.</li>
 * <li>Apply the same XZ-centering math {@link FreePlacementResolver} uses so the structure is centered on the cursor
 * horizontally.</li>
 * </ol>
 * {@code MOTION_BLOCKING_NO_LEAVES} is preferred over {@code WORLD_SURFACE} because it skips leaf blocks — a structure
 * landing on a tree canopy is rarely what the user wants, and the under-canopy ground is reachable through the leaves.
 */
@ApiStatus.Internal
public final class SurfaceSnapResolver implements PlacementResolver {

    public static final SurfaceSnapResolver INSTANCE = new SurfaceSnapResolver();

    private SurfaceSnapResolver() {}

    @Override
    public @Nullable Placement resolve(PlacementContext ctx) {
        var hit = JigsawPlacementCursor.clipFromCursor(ctx.session());
        if (hit == null) {
            return null;
        }

        var mc = Minecraft.getInstance();
        if (mc.level == null) {
            return null;
        }

        var hitPos = hit.getBlockPos();
        var surfaceY = mc.level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, hitPos.getX(), hitPos.getZ());

        // Same centering math as FREE — the only thing surface-snap changes is the Y source.
        var settings = new StructurePlaceSettings().setRotation(ctx.userRotation()).setMirror(ctx.userMirror());
        var baseBbox = ctx.template().getBoundingBox(settings, BlockPos.ZERO);
        var centerX = (baseBbox.minX() + baseBbox.maxX()) / 2;
        var centerZ = (baseBbox.minZ() + baseBbox.maxZ()) / 2;
        var minY = baseBbox.minY();

        var anchor = new BlockPos(hitPos.getX() - centerX, surfaceY - minY, hitPos.getZ() - centerZ);
        return new Placement(anchor, ctx.userRotation(), ctx.userMirror());
    }
}
