package com.avp.common.explosion;

import net.minecraft.core.BlockPos;

@FunctionalInterface
public interface ExplosionBlockSampler {

    void sample(Explosion explosion, BlockPos pos);
}
