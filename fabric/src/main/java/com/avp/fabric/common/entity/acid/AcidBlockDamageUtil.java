package com.avp.fabric.common.entity.acid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import com.avp.common.block.AVPBlockTags;
import com.avp.fabric.common.sound.AVPSoundEvents;
import com.avp.server.BlockBreakProgressManager;

public class AcidBlockDamageUtil {

    public static void damageBlocks(Acid acid) {
        var level = acid.level();

        BlockPos.betweenClosedStream(acid.getBoundingBox().inflate(0, 0.1, 0))
            .filter(blockPos -> canAcidDestroyBlock(acid, blockPos, level))
            .forEach(blockPos -> tryDestroyBlock(acid, blockPos, level));
    }

    private static void tryDestroyBlock(Acid acid, BlockPos blockPos, Level level) {
        if (!level.isClientSide) {
            if (acid.isInWater() || !acid.onGround()) {
                return;
            }

            damageBlock(acid, blockPos, level);
        } else {
            spawnClientSideParticles(acid, level);
        }
    }

    private static void damageBlock(Acid acid, BlockPos blockPos, Level level) {
        if (acid.isIrradiated() && !level.getBlockState(blockPos).is(AVPBlockTags.IRRADIATED_ACID_IMMUNE)) {
            level.setBlockAndUpdate(blockPos, Blocks.BLUE_ICE.defaultBlockState());
        } else {
            if (acid.isNetherAfflicted() && level.getBlockState(blockPos).isAir()) {
                level.setBlockAndUpdate(blockPos, Blocks.FIRE.defaultBlockState());
            }

            BlockBreakProgressManager.damage(level, blockPos, acid.getMultiplier());
        }

        if (acid.tickCount % (acid.getRandom().nextInt(100) + 10) == 0) {
            level.playSound(null, acid, AVPSoundEvents.BLOCK_ACID_BURN, SoundSource.NEUTRAL, 1F, 1F);
        }

        acid.age();
    }

    private static void spawnClientSideParticles(Acid acid, Level level) {
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

    private static boolean canAcidDestroyBlock(Acid acid, BlockPos blockPos, Level level) {
        var blockState = level.getBlockState(blockPos);

        if (blockState.isAir()) {
            return false;
        }

        if (acid.isNetherAfflicted()) {
            return !blockState.is(AVPBlockTags.NETHER_ACID_IMMUNE);
        }

        if (acid.isIrradiated()) {
            return !blockState.is(AVPBlockTags.IRRADIATED_ACID_IMMUNE);
        }

        return !blockState.is(AVPBlockTags.ACID_IMMUNE);
    }
}
