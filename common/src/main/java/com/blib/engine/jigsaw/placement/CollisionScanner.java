package com.blib.engine.jigsaw.placement;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Counts the world blocks a {@link Placement} would overwrite. Walks the active palette's transformed block list and
 * queries the level's existing block state at each resulting world position.
 * <p>
 * Iteration is template-bound rather than AABB-bound — for sparse templates (which most jigsaw pieces are) this is
 * strictly fewer block lookups than an AABB walk would be, and {@code STRUCTURE_VOID} is naturally skipped by the
 * upstream {@link TransformedTemplateCache} because vanilla's {@code placeInWorld} doesn't write at those positions.
 * <p>
 * For {@link PlacementMode#JIGSAW_SNAP}, the resolver places the candidate's jigsaw block at {@code anchor.worldPos +
 * anchor.front} — that single connecting cell is exempt from the count because the face-to-face geometry makes
 * "overlap" there expected. Without the exemption, every snap-placement would count the connection block as a collision
 * and the user couldn't tell the difference between intended and unintended overlaps.
 * <p>
 * The scanner caches its most recent result by (template, dimension, anchor, rotation, mirror, exemptPos). On the
 * common case where the cursor is held still — the camera might still micro-drift, but the resolved anchor block
 * doesn't change every frame — subsequent calls return immediately without re-walking the palette or hitting the level.
 * Engine mode pauses the integrated server, so the world cannot change underneath a cached entry; on workspace close,
 * callers can {@link #invalidate} to drop the cache defensively.
 */
@ApiStatus.Internal
public final class CollisionScanner {

    private record ScanKey(
        StructureTemplate template,
        ResourceKey<Level> dimension,
        BlockPos anchor,
        Rotation rotation,
        Mirror mirror,
        @Nullable BlockPos exemptPos
    ) {}

    private static @Nullable ScanKey cachedKey;

    private static int cachedCount;

    private CollisionScanner() {}

    /**
     * Returns the number of non-air world blocks the placement would overwrite. {@code snapAnchor} should be the jigsaw
     * block the resolver snapped to in {@link PlacementMode#JIGSAW_SNAP}, or {@code null} for any other mode — the
     * connection-block exemption only fires when an anchor is supplied.
     */
    public static int scan(Level level, Placement placement, StructureTemplate template, @Nullable JigsawBlockTarget snapAnchor) {
        var rotation = placement.rotation();
        var mirror = placement.mirror();
        var placementAnchor = placement.anchor();

        BlockPos exemptPos = null;
        if (snapAnchor != null) {
            exemptPos = snapAnchor.worldPos().relative(snapAnchor.front());
        }

        var key = new ScanKey(template, level.dimension(), placementAnchor, rotation, mirror, exemptPos);
        if (key.equals(cachedKey)) {
            return cachedCount;
        }

        var blocks = TransformedTemplateCache.get(template, rotation, mirror);
        if (blocks.isEmpty()) {
            cachedKey = key;
            cachedCount = 0;
            return 0;
        }

        int count = 0;
        var mutable = new BlockPos.MutableBlockPos();
        for (var block : blocks) {
            var local = block.localPos();
            mutable.set(
                placementAnchor.getX() + local.getX(),
                placementAnchor.getY() + local.getY(),
                placementAnchor.getZ() + local.getZ()
            );

            if (exemptPos != null && mutable.equals(exemptPos)) {
                continue;
            }

            if (!level.getBlockState(mutable).isAir()) {
                count++;
            }
        }

        cachedKey = key;
        cachedCount = count;
        return count;
    }

    /** Drop the cached result. Safe to call from workspace open/close or any other lifecycle hook. */
    public static void invalidate() {
        cachedKey = null;
        cachedCount = 0;
    }
}
