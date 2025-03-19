package com.avp.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;

import com.avp.common.worldgen.biome.AVPBiomes;

public class AshBlock extends SnowLayerBlock {

    public AshBlock(Properties properties) {
        super(properties);
        this.registerDefaultState((this.stateDefinition.any()).setValue(LAYERS, 1));
    }

    @Override
    protected boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        return levelReader.getBiome(blockPos).is(AVPBiomes.NUKED_BIOME);
    }

    @Override
    protected void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if (randomSource.nextInt(10) == 0) {
            if (blockState.getValue(LAYERS) < 8) {
                serverLevel.setBlockAndUpdate(
                    blockPos,
                    this.defaultBlockState().setValue(LAYERS, blockState.getValue(LAYERS) + 1)
                );
            } else {
                for (var dx = -1; dx <= 1; dx++) {
                    for (var dz = -1; dz <= 1; dz++) {
                        var targetPos = blockPos.offset(dx, 0, dz);
                        var targetPosBelow = targetPos.below();

                        if (
                            serverLevel.getBlockState(targetPos).isAir() &&
                                serverLevel.getBlockState(targetPosBelow).isSolidRender(serverLevel, targetPosBelow)
                                && !serverLevel.getBlockState(targetPosBelow).is(AVPBlocks.ASH_BLOCK)
                        ) {
                            serverLevel.setBlockAndUpdate(
                                targetPos,
                                this.defaultBlockState().setValue(LAYERS, 1)
                            );
                            return;
                        }
                    }
                }
            }
        }
    }
}
