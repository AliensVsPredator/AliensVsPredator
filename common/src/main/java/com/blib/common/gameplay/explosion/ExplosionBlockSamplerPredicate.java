package com.blib.common.gameplay.explosion;

import com.blib.common.util.ExplosionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

@FunctionalInterface
public interface ExplosionBlockSamplerPredicate {

    boolean test(Explosion explosion, BlockPos pos);

    ExplosionBlockSamplerPredicate DEFAULT = (explosion, pos) -> {
        var level = explosion.level();
        var state = level.getBlockState(pos);

        var centerPos = explosion.config().centerBlockPosition();

        if (state.is(Blocks.AIR) || state.is(Blocks.BEDROCK)) {
            return false;
        }

        var x = pos.getX() - centerPos.getX();
        var y = pos.getY() - centerPos.getY();
        var z = pos.getZ() - centerPos.getZ();

        return ExplosionUtil.getNormalizedDistance(explosion, x, y, z) <= 1;
    };
}
