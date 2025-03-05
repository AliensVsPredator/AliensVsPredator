package com.avp.core.common.block.resin;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import com.avp.core.common.block.AVPBlocks;
import com.avp.core.common.block.entity.resin_node.ChargeCursor;
import com.avp.core.common.block.entity.resin_node.ResinSpreader;
import com.avp.core.common.block.entity.resin_node.behavior.SpreadBehavior;

public class ResinVeinBlock extends MultifaceBlock implements SpreadBehavior {

    public static final MapCodec<MultifaceBlock> CODEC = simpleCodec(ResinVeinBlock::new);

    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private final MultifaceSpreader veinSpreader = new MultifaceSpreader(
        new ResinVeinSpreaderConfig(this, MultifaceSpreader.DEFAULT_SPREAD_ORDER)
    );

    private final MultifaceSpreader sameSpaceSpreader = new MultifaceSpreader(
        new ResinVeinSpreaderConfig(this, MultifaceSpreader.SpreadType.SAME_POSITION)
    );

    public ResinVeinBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    protected @NotNull MapCodec<MultifaceBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    protected @NotNull BlockState updateShape(
        BlockState blockState,
        Direction direction,
        BlockState blockState2,
        LevelAccessor levelAccessor,
        BlockPos blockPos,
        BlockPos blockPos2
    ) {
        if (blockState.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }

        return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
    }

    @Override
    protected boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
        return !blockPlaceContext.getItemInHand().is(AVPBlocks.RESIN_VEIN.get().asItem()) || super.canBeReplaced(blockState, blockPlaceContext);
    }

    @Override
    public @NotNull MultifaceSpreader getSpreader() {
        return veinSpreader;
    }

    public MultifaceSpreader getSameSpaceSpreader() {
        return sameSpaceSpreader;
    }

    @Override
    protected @NotNull FluidState getFluidState(BlockState blockState) {
        return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
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
        if (bl && this.attemptPlaceResin(resinSpreader, levelAccessor, chargeCursor.getPos(), randomSource)) {
            return chargeCursor.getCharge() - 1;
        } else {
            return randomSource.nextInt(resinSpreader.chargeDecayRate()) == 0
                ? Mth.floor((float) chargeCursor.getCharge() * 0.5F)
                : chargeCursor.getCharge();
        }
    }

    private boolean attemptPlaceResin(
        ResinSpreader resinSpreader,
        LevelAccessor levelAccessor,
        BlockPos blockPos,
        RandomSource randomSource
    ) {
        var blockState = levelAccessor.getBlockState(blockPos);
        var tagKey = resinSpreader.replaceableBlocks();

        for (var direction : Direction.allShuffled(randomSource)) {
            if (!hasFace(blockState, direction)) {
                continue;
            }

            var blockPos2 = blockPos.relative(direction);
            var blockState2 = levelAccessor.getBlockState(blockPos2);

            if (!blockState2.is(tagKey)) {
                continue;
            }

            // TODO: Try not to hardcode this.
            var resinBlock = this == AVPBlocks.RESIN_VEIN.get() ? AVPBlocks.RESIN.get() : AVPBlocks.NETHER_RESIN.get();
            var resinBlockState = resinBlock.defaultBlockState();

            levelAccessor.setBlock(blockPos2, resinBlockState, 3);
            Block.pushEntitiesUp(blockState2, resinBlockState, levelAccessor, blockPos2);
            // TODO:
            levelAccessor.playSound(null, blockPos2, SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.BLOCKS, 1.0F, 1.0F);
            this.veinSpreader.spreadAll(resinBlockState, levelAccessor, blockPos2, resinSpreader.isWorldGeneration());
            var direction2 = direction.getOpposite();

            dischargeForAllFacesExcludingDirection(levelAccessor, randomSource, direction2, blockPos2);

            return true;
        }

        return false;
    }

    private void dischargeForAllFacesExcludingDirection(
        LevelAccessor levelAccessor,
        RandomSource randomSource,
        Direction direction2,
        BlockPos blockPos2
    ) {
        for (var direction3 : DIRECTIONS) {
            if (direction3 == direction2) {
                continue;
            }

            var blockPos3 = blockPos2.relative(direction3);
            dischargeForBlockPos(levelAccessor, randomSource, blockPos3);
        }
    }

    private void dischargeForBlockPos(LevelAccessor levelAccessor, RandomSource randomSource, BlockPos blockPos3) {
        var blockState4 = levelAccessor.getBlockState(blockPos3);

        if (blockState4.is(this)) {
            this.onDischarged(levelAccessor, blockState4, blockPos3, randomSource);
        }
    }
}
