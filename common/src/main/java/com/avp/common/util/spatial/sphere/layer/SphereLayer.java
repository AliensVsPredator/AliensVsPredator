package com.avp.common.util.spatial.sphere.layer;

import com.avp.common.util.spatial.Vec3Like;

public interface SphereLayer {

    float getMinNormalizedRadius();

    float getMaxNormalizedRadius();

    default boolean contains(LayeredSphere sphere, Vec3Like point) {
        var distSqr = point.distanceSquaredTo(sphere.getCenter());
        var minRadius = sphere.getRadius() * getMinNormalizedRadius();
        var maxRadius = sphere.getRadius() * getMaxNormalizedRadius();

        var minSqr = minRadius * minRadius;
        var maxSqr = maxRadius * maxRadius;

        return distSqr >= minSqr && distSqr < maxSqr;
    }

    default boolean containsFromCenterToOuterEdge(LayeredSphere sphere, Vec3Like pos) {
        var distSqr = pos.distanceSquaredTo(sphere.getCenter());
        var outer = sphere.getRadius() * getMaxNormalizedRadius();
        var outerSqr = outer * outer;

        return distSqr <= outerSqr;
    }

    default double distanceSquaredTo(LayeredSphere sphere, Vec3Like pos, SphereLayerDistanceTarget target) {
        var distToCenterSqr = pos.distanceSquaredTo(sphere.getCenter());
        var radius = sphere.getRadius();

        var minSqr = Math.pow(radius * getMinNormalizedRadius(), 2);
        var maxSqr = Math.pow(radius * getMaxNormalizedRadius(), 2);

        return switch (target) {
            case INNER_BOUNDARY -> Math.abs(distToCenterSqr - minSqr);
            case OUTER_BOUNDARY -> Math.abs(distToCenterSqr - maxSqr);
            case NEAREST_BOUNDARY -> Math.min(
                Math.abs(distToCenterSqr - minSqr),
                Math.abs(distToCenterSqr - maxSqr)
            );
        };
    }
}
