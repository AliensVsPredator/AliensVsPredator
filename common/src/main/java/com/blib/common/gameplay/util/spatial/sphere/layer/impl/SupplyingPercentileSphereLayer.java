package com.blib.common.gameplay.util.spatial.sphere.layer.impl;

import com.blib.common.gameplay.util.spatial.sphere.layer.SphereLayer;

import java.util.function.Supplier;

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
