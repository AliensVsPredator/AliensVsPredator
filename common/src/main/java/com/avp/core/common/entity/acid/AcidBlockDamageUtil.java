package com.avp.core.common.entity.acid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;

import com.avp.core.common.block.AVPBlockTags;
import com.avp.core.common.sound.AVPSoundEvents;
import com.avp.core.server.BlockBreakProgressManager;

public class AcidBlockDamageUtil {

    public static void damageBlocks(Acid acid) {
        var level = acid.level();

        if (!level.isClientSide && (acid.isInWater() || !acid.onGround())) {
            return;
        }

        BlockPos.betweenClosedStream(acid.getBoundingBox().inflate(0, 0.1, 0))
            .filter(blockPos -> {
                var blockState = level.getBlockState(blockPos);
                var netherCheck = acid.isNetherAfflicted() && blockState.is(AVPBlockTags.NETHER_ACID_IMMUNE);

                return !blockState.is(AVPBlockTags.ACID_IMMUNE)
                    && !netherCheck;
            })
            .forEach(blockPos -> {
                if (!level.isClientSide) {
                    BlockBreakProgressManager.damage(level, blockPos, acid.getMultiplier());

                    if (acid.tickCount % (acid.getRandom().nextInt(100) + 10) == 0) {
                        level.playSound(null, acid, AVPSoundEvents.BLOCK_ACID_BURN.get(), SoundSource.NEUTRAL, 1F, 1F);
                    }

                    // Acid disappears twice as fast when in water.
                    acid.age();
                } else {
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
