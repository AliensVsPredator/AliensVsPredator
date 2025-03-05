package com.avp.common.block.resin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import com.avp.common.block.AVPBlocks;

class ResinVeinSpreaderConfig extends MultifaceSpreader.DefaultSpreaderConfig {

    private final MultifaceSpreader.SpreadType[] spreadTypes;

    public ResinVeinSpreaderConfig(ResinVeinBlock resinVeinBlock, final MultifaceSpreader.SpreadType... spreadTypes) {
        super(resinVeinBlock);
        this.spreadTypes = spreadTypes;
    }

    @Override
    public boolean stateCanBeReplaced(
        BlockGetter blockGetter,
        BlockPos blockPos,
        BlockPos blockPos2,
        Direction direction,
        BlockState blockState
    ) {
        var blockState2 = blockGetter.getBlockState(blockPos2.relative(direction));
        var isNotResinNode = !blockState2.is(AVPBlocks.NETHER_RESIN_NODE) && !blockState2.is(AVPBlocks.RESIN_NODE);
        var canReplace = !blockState2.is(AVPBlocks.RESIN) && isNotResinNode && !blockState2.is(Blocks.MOVING_PISTON);

        if (!canReplace) {
            return false;
        }

        if (blockPos.distManhattan(blockPos2) == 2) {
            var blockPos3 = blockPos.relative(direction.getOpposite());

            if (blockGetter.getBlockState(blockPos3).isFaceSturdy(blockGetter, blockPos3, direction)) {
                return false;
            }
        }

        var fluidState = blockState.getFluidState();

        if ((!fluidState.isEmpty() && !fluidState.is(Fluids.WATER)) || blockState.is(BlockTags.FIRE)) {
            return false;
        } else {
            return blockState.canBeReplaced() || super.stateCanBeReplaced(blockGetter, blockPos, blockPos2, direction, blockState);
        }
    }

    @Override
    public MultifaceSpreader.SpreadType @NotNull [] getSpreadTypes() {
        return this.spreadTypes;
    }

    @Override
    public boolean isOtherBlockValidAsSource(BlockState blockState) {
        return !blockState.is(AVPBlocks.RESIN_VEIN);
    }
}
