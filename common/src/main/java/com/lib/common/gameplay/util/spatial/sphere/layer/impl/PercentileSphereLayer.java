package com.lib.common.gameplay.util.spatial.sphere.layer.impl;

import com.lib.common.gameplay.util.spatial.sphere.layer.SphereLayer;

public final class PercentileSphereLayer implements SphereLayer {

    private final float minNormalized;

    private final float maxNormalized;

    public PercentileSphereLayer(float minNormalized, float maxNormalized) {
        this.minNormalized = minNormalized;
        this.maxNormalized = maxNormalized;
    }

    @Override
    public float getMinNormalizedRadius() {
        return minNormalized;
    }

    @Override
    public float getMaxNormalizedRadius() {
        return maxNormalized;
    }
}
