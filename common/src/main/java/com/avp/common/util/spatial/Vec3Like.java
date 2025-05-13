package com.avp.common.util.spatial;

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
