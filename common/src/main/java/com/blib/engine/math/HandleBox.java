package com.blib.engine.math;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

/**
 * Axis-aligned bounding box in world space, expressed as a pair of min/max corners. Generic counterpart to vanilla's
 * {@code AABB} that gizmo picking code can use without pulling in the vanilla type. Renderer and picker share this so
 * the rendered geometry matches the hit box exactly.
 */
@ApiStatus.Internal
public record HandleBox(
    double minX,
    double minY,
    double minZ,
    double maxX,
    double maxY,
    double maxZ
) {

    public static HandleBox cube(Vec3 center, double halfExtent) {
        return new HandleBox(
            center.x - halfExtent,
            center.y - halfExtent,
            center.z - halfExtent,
            center.x + halfExtent,
            center.y + halfExtent,
            center.z + halfExtent
        );
    }
}
