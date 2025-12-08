package com.blib.common.gameplay.util.spatial.sphere.layer;

import com.blib.common.gameplay.util.spatial.Vec3Like;
import com.blib.common.gameplay.util.spatial.sphere.Sphere;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface LayeredSphere extends Sphere {

    List<SphereLayer> getAllLayers();

    default boolean isWithinLayerOrBelow(SphereLayer layer, Vec3Like pos) {
        var distSqr = pos.distanceSquaredTo(getCenter());
        var outerSqr = Math.pow(getRadius() * layer.getMaxNormalizedRadius(), 2);

        return distSqr <= outerSqr;
    }

    default boolean isBetweenLayers(SphereLayer inner, SphereLayer outer, Vec3Like pos) {
        var distSqr = pos.distanceSquaredTo(getCenter());
        var minSqr = Math.pow(getRadius() * inner.getMinNormalizedRadius(), 2);
        var maxSqr = Math.pow(getRadius() * outer.getMaxNormalizedRadius(), 2);

        return distSqr >= minSqr && distSqr <= maxSqr;
    }

    default @Nullable SphereLayer getLayerOrNull(Vec3Like pos) {
        for (var layer : getAllLayers()) {
            if (layer.contains(this, pos)) {
                return layer;
            }
        }

        return null;
    }
}
