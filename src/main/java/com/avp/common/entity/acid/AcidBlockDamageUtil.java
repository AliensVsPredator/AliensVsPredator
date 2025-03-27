package com.avp.common.entity.acid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;

import com.avp.common.block.AVPBlockTags;
import com.avp.common.sound.AVPSoundEvents;
import com.avp.server.BlockBreakProgressManager;
import net.minecraft.world.level.block.Blocks;

public class AcidBlockDamageUtil {

    public static void damageBlocks(Acid acid) {
        var level = acid.level();

        if (!level.isClientSide && (acid.isInWater() || !acid.onGround())) {
            return;
        }

        BlockPos.betweenClosedStream(acid.getBoundingBox().inflate(0, 0.1, 0))
            .filter(blockPos -> {
                var blockState = level.getBlockState(blockPos);

                if (acid.isNetherAfflicted()) {
                    return !blockState.is(AVPBlockTags.NETHER_ACID_IMMUNE);
                }

                if (acid.isIrradiated()) {
                    return blockState.canBeReplaced();
                }

                return !blockState.is(AVPBlockTags.ACID_IMMUNE);
            })
            .forEach(blockPos -> {
                if (!level.isClientSide) {
                    if (acid.isIrradiated()) {
                        level.setBlockAndUpdate(blockPos.below(), Blocks.BLUE_ICE.defaultBlockState());
                    } else {
                        BlockBreakProgressManager.damage(level, blockPos, acid.getMultiplier());
                    }

                    if (acid.tickCount % (acid.getRandom().nextInt(100) + 10) == 0) {
                        level.playSound(null, acid, AVPSoundEvents.BLOCK_ACID_BURN, SoundSource.NEUTRAL, 1F, 1F);
                    }

                    // Acid disappears twice as fast when in water.
                    acid.age();
                } else {
                    if (acid.isIrradiated()) {
                        return;
                    }
                    level.addAlwaysVisibleParticle(
                        ParticleTypes.SMOKE,
                        acid.getRandomX(0.5),
                        acid.getRandomY(),
                        acid.getRandomZ(0.5),
                        0,
                        0,
                        0
                    );
                }
            });
    }
}
