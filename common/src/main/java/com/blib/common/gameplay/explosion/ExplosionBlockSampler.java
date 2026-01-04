package com.blib.common.gameplay.explosion;

import net.minecraft.core.BlockPos;

@FunctionalInterface
public interface ExplosionBlockSampler {

    void sample(Explosion explosion, BlockPos pos);
}
