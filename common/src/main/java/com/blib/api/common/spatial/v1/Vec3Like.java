package com.blib.api.common.spatial.v1;

/**
 * Represents a 3D coordinate with x, y, and z components.
 */
public interface Vec3Like {

    double x();

    double y();

    double z();

    default double distanceSquaredTo(Vec3Like other) {
        var dx = x() - other.x();
        var dy = y() - other.y();
        var dz = z() - other.z();
        return dx * dx + dy * dy + dz * dz;
    }
}
