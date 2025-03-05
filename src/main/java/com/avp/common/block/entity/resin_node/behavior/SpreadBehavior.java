package com.avp.common.block.entity.resin_node.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import com.avp.common.block.AVPBlocks;
import com.avp.common.block.entity.resin_node.ChargeCursor;
import com.avp.common.block.entity.resin_node.ResinSpreader;

public interface SpreadBehavior {

    default byte getResinSpreadDelay() {
        return 1;
    }

    default void onDischarged(LevelAccessor levelAccessor, BlockState blockState, BlockPos blockPos, RandomSource randomSource) {}

    default boolean attemptSpreadVein(
        BlockPos nodePos,
        LevelAccessor levelAccessor,
        BlockPos blockPos,
        BlockState blockState,
        @Nullable Collection<Direction> facings,
        boolean bl
    ) {
        var block = levelAccessor.getBlockState(nodePos).getBlock();
        // TODO: Try not to hardcode this.
        var resinBlock = block == AVPBlocks.NETHER_RESIN_NODE ? AVPBlocks.NETHER_RESIN_VEIN : AVPBlocks.RESIN_VEIN;

        return ((MultifaceBlock) resinBlock).getSpreader().spreadAll(blockState, levelAccessor, blockPos, bl) > 0L;
    }

    default boolean canChangeBlockStateOnSpread() {
        return true;
    }

    default int updateDecayDelay(int decayDelay) {
        return 1;
    }

    int attemptUseCharge(
        ChargeCursor chargeCursor,
        LevelAccessor levelAccessor,
        BlockPos blockPos,
        RandomSource randomSource,
        ResinSpreader resinSpreader,
        boolean bl
    );
}
