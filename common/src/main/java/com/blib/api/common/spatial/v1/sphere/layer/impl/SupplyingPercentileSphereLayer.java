package com.blib.api.common.spatial.v1.sphere.layer.impl;

import java.util.function.Supplier;

import com.blib.api.common.spatial.v1.sphere.layer.SphereLayer;

public final class SupplyingPercentileSphereLayer implements SphereLayer {

    private final Supplier<Float> minNormalizedSupplier;

    private final Supplier<Float> maxNormalizedSupplier;

    public SupplyingPercentileSphereLayer(Supplier<Float> minNormalizedSupplier, Supplier<Float> maxNormalizedSupplier) {
        this.minNormalizedSupplier = minNormalizedSupplier;
        this.maxNormalizedSupplier = maxNormalizedSupplier;
    }

    @Override
    public float getMinNormalizedRadius() {
        return minNormalizedSupplier.get();
    }

    @Override
    public float getMaxNormalizedRadius() {
        return maxNormalizedSupplier.get();
    }
}
