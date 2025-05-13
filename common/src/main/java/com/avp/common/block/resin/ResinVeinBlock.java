package com.avp.common.block.resin;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

import com.avp.common.block.entity.resin_node.ChargeCursor;
import com.avp.common.block.entity.resin_node.ResinSpreader;
import com.avp.common.block.entity.resin_node.behavior.VeinSpreadBehavior;
import com.avp.common.entity.living.alien.AlienVariantTypes;
import com.avp.common.sound.AVPSoundEvents;

public class ResinVeinBlock extends MultifaceBlock implements VeinSpreadBehavior {

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
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    protected @NotNull BlockState updateShape(
        BlockState blockState,
        @NotNull Direction direction,
        @NotNull BlockState blockState2,
        @NotNull LevelAccessor levelAccessor,
        @NotNull BlockPos blockPos,
        @NotNull BlockPos blockPos2
    ) {
        if (blockState.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }

        return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
    }

    @Override
    protected boolean canBeReplaced(@NotNull BlockState blockState, @NotNull BlockPlaceContext blockPlaceContext) {
        var alienVariantType = AlienVariantTypes.getForOrNull(blockState);
        // This check is faster than super.canBeReplaced, which checks all block face directions.
        var itemInHandIsNotResinVein = alienVariantType != null && !blockPlaceContext.getItemInHand()
            .is(alienVariantType.resinVein().get().asItem());
        return itemInHandIsNotResinVein || super.canBeReplaced(blockState, blockPlaceContext);
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
        return blockState.getValue(WATERLOGGED)
            ? Fluids.WATER.getSource(false)
            : super.getFluidState(blockState);
    }

    @Override
    public int attemptUseCharge(
        ChargeCursor chargeCursor,
        LevelAccessor levelAccessor,
        BlockPos nodePos,
        RandomSource randomSource,
        ResinSpreader resinSpreader
    ) {
        if (attemptPlaceResin(levelAccessor, nodePos, chargeCursor.getPos(), randomSource)) {
            return chargeCursor.getCharge() - 1;
        } else {
            return randomSource.nextInt(resinSpreader.chargeDecayRate()) == 0
                ? Mth.floor(chargeCursor.getCharge() * 0.5F)
                : chargeCursor.getCharge();
        }
    }

    private boolean attemptPlaceResin(LevelAccessor levelAccessor, BlockPos nodePos, BlockPos cursorPos, RandomSource randomSource) {
        var nodeBlockState = levelAccessor.getBlockState(nodePos);
        var cursorBlockState = levelAccessor.getBlockState(cursorPos);
        var alienVariantType = AlienVariantTypes.getForOrNull(nodeBlockState);

        if (alienVariantType == null) {
            return false;
        }

        var resinBlockState = alienVariantType.resin().get().defaultBlockState();

        for (var direction : Direction.allShuffled(randomSource)) {
            if (!hasFace(cursorBlockState, direction)) {
                continue;
            }

            var blockPos2 = cursorPos.relative(direction);
            var blockState2 = levelAccessor.getBlockState(blockPos2);

            if (!blockState2.is(alienVariantType.resinReplaceableTag())) {
                continue;
            }

            levelAccessor.setBlock(blockPos2, resinBlockState, 3);
            Block.pushEntitiesUp(blockState2, resinBlockState, levelAccessor, blockPos2);
            levelAccessor.playSound(null, blockPos2, AVPSoundEvents.BLOCK_RESIN_SPREAD.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            veinSpreader.spreadAll(resinBlockState, levelAccessor, blockPos2, false);

            return true;
        }

        return false;
    }
}
