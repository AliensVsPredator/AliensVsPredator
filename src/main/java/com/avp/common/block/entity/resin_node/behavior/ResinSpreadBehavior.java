package com.avp.common.block.entity.resin_node.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import com.avp.common.block.AVPBlocks;
import com.avp.common.block.entity.resin_node.ChargeCursor;
import com.avp.common.block.entity.resin_node.ResinSpreader;
import com.avp.common.block.resin.ResinVeinBlock;
import com.avp.common.block.resin.ResinVeinRegrowUtil;

public class ResinSpreadBehavior implements SpreadBehavior {

    public static final ResinSpreadBehavior INSTANCE = new ResinSpreadBehavior();

    private ResinSpreadBehavior() {}

    @Override
    public boolean attemptSpreadVein(
        BlockPos nodePos,
        LevelAccessor levelAccessor,
        BlockPos blockPos,
        BlockState blockState,
        @Nullable Collection<Direction> facings,
        boolean bl
    ) {
        var nodeBlock = levelAccessor.getBlockState(nodePos).getBlock();
        // TODO: Try not to hardcode this.
        var resinBlock = (ResinVeinBlock) (nodeBlock == AVPBlocks.NETHER_RESIN_NODE ? AVPBlocks.NETHER_RESIN_VEIN : AVPBlocks.RESIN_VEIN);

        if (facings == null) {
            var spreader = resinBlock.getSameSpaceSpreader();
            return spreader.spreadAll(levelAccessor.getBlockState(blockPos), levelAccessor, blockPos, bl) > 0L;
        } else if (!facings.isEmpty()) {
            return isAirOrWater(blockState) && ResinVeinRegrowUtil.regrow(
                resinBlock.defaultBlockState(),
                levelAccessor,
                blockPos,
                blockState,
                facings
            );
        } else {
            return SpreadBehavior.super.attemptSpreadVein(nodePos, levelAccessor, blockPos, blockState, facings, bl);
        }
    }

    @Override
    public int attemptUseCharge(
        ChargeCursor chargeCursor,
        LevelAccessor levelAccessor,
        BlockPos blockPos,
        RandomSource randomSource,
        ResinSpreader resinSpreader,
        boolean bl
    ) {
        return chargeCursor.getDecayDelay() > 0 ? chargeCursor.getCharge() : 0;
    }

    @Override
    public int updateDecayDelay(int decayDelay) {
        return Math.max(decayDelay - 1, 0);
    }

    private boolean isAirOrWater(BlockState blockState) {
        return blockState.isAir() || blockState.getFluidState().is(Fluids.WATER);
    }
}
