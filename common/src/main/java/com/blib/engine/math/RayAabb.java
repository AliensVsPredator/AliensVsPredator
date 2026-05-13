package com.blib.engine.math;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

/**
 * Ray-vs-AABB intersection. Single source of truth for the slab method used by every gizmo's pick logic. Prior to this
 * utility the same 27 lines existed verbatim in {@code BlockSelectionScaleGizmo}, {@code BlockSelectionTranslateGizmo},
 * {@code MoveBlocksGizmo}, {@code EntityScaleGizmo}, and {@code EntityTranslateGizmo}.
 */
@ApiStatus.Internal
public final class RayAabb {

    private RayAabb() {}

    /**
     * Returns the parametric {@code t} of the first ray-box hit (positive only), or {@link Double#NaN} if the ray
     * misses. {@code rayDir} need not be normalized — {@code t} is in units of the input direction.
     */
    public static double intersect(Vec3 origin, Vec3 dir, HandleBox box) {
        return intersect(origin.x, origin.y, origin.z, dir.x, dir.y, dir.z, box);
    }

    public static double intersect(double ox, double oy, double oz, double dx, double dy, double dz, HandleBox box) {
        return intersect(ox, oy, oz, dx, dy, dz, box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ());
    }

    /** Doubles-only overload — call this from inner picking loops to avoid allocating a {@link HandleBox} per frame. */
    public static double intersect(
        double ox,
        double oy,
        double oz,
        double dx,
        double dy,
        double dz,
        double minX,
        double minY,
        double minZ,
        double maxX,
        double maxY,
        double maxZ
    ) {
        var tMin = Double.NEGATIVE_INFINITY;
        var tMax = Double.POSITIVE_INFINITY;
        for (var i = 0; i < 3; i++) {
            var o = i == 0 ? ox : (i == 1 ? oy : oz);
            var d = i == 0 ? dx : (i == 1 ? dy : dz);
            var min = i == 0 ? minX : (i == 1 ? minY : minZ);
            var max = i == 0 ? maxX : (i == 1 ? maxY : maxZ);
            if (Math.abs(d) < 1.0e-9) {
                if (o < min || o > max) {
                    return Double.NaN;
                }
                continue;
            }
            var t1 = (min - o) / d;
            var t2 = (max - o) / d;
            if (t1 > t2) {
                var swap = t1;
                t1 = t2;
                t2 = swap;
            }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
            if (tMin > tMax) {
                return Double.NaN;
            }
        }
        return tMin > 0 ? tMin : tMax;
    }
}
