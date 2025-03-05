package com.avp.core.common.block.resin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Collection;

public class ResinVeinRegrowUtil {

    public static boolean regrow(
        BlockState blockState2,
        LevelAccessor levelAccessor,
        BlockPos blockPos,
        BlockState targetBlockState,
        Collection<Direction> collection
    ) {
        var foundGrowableFace = false;

        for (var direction : collection) {
            var blockPos2 = blockPos.relative(direction);
            if (MultifaceBlock.canAttachTo(levelAccessor, direction, blockPos2, levelAccessor.getBlockState(blockPos2))) {
                blockState2 = blockState2.setValue(MultifaceBlock.getFaceProperty(direction), Boolean.TRUE);
                foundGrowableFace = true;
            }
        }

        if (!foundGrowableFace) {
            return false;
        } else {
            if (!targetBlockState.getFluidState().isEmpty()) {
                blockState2 = blockState2.setValue(BlockStateProperties.WATERLOGGED, Boolean.TRUE);
            }

            levelAccessor.setBlock(blockPos, blockState2, 3);
            return true;
        }
    }
}
