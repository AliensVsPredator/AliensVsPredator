package com.avp.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class LithiumBlock extends Block {

    public LithiumBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void neighborChanged(
        @NotNull BlockState blockState,
        @NotNull Level level,
        @NotNull BlockPos blockPos,
        @NotNull Block previousBlock,
        @NotNull BlockPos updatedPos,
        boolean b
    ) {
        super.neighborChanged(blockState, level, blockPos, previousBlock, updatedPos, b);

        var newBlockState = level.getBlockState(updatedPos);

        if (newBlockState.is(Blocks.WATER)) {
            var strength = blockState.is(AVPBlocks.LITHIUM_ORE) ? 2F : 4F;
            level.removeBlock(blockPos, false);
            level.explode(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), strength, Level.ExplosionInteraction.BLOCK);
        }
    }
}
