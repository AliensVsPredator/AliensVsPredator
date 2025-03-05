package com.avp.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import com.avp.common.util.BlockPosUtil;

public class ResinBlock extends Block {

    public ResinBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        super.onRemove(blockState, level, blockPos, blockState2, bl);

        if (BlockPosUtil.isFireAdjacent(level, blockPos)) {
            level.setBlock(blockPos, Blocks.BASALT.defaultBlockState(), 3);
        }
    }
}
