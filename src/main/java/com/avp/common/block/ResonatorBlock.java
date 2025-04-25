package com.avp.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.avp.common.block.entity.AVPBlockEntityTypes;
import com.avp.common.block.entity.ResonatorBlockEntity;

public class ResonatorBlock extends BaseEntityBlock {

    public static final MapCodec<ResonatorBlock> CODEC = simpleCodec(ResonatorBlock::new);

    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    protected ResonatorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(TRIGGERED, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TRIGGERED);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ResonatorBlockEntity(blockPos, blockState);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return ResonatorBlock.createServerTicker(level, blockEntityType);
    }

    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createServerTicker(
        Level level,
        BlockEntityType<T> blockEntityType
    ) {
        return level.isClientSide
            ? null
            : createTickerHelper(
                blockEntityType,
                (BlockEntityType<? extends ResonatorBlockEntity>) AVPBlockEntityTypes.RESONATOR,
                ResonatorBlockEntity::serverTick
            );
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        BlockHitResult hitResult
    ) {
        if (!level.isClientSide) {
            var be = level.getBlockEntity(pos);
            if (be instanceof ResonatorBlockEntity resonatorBlockEntity) {
                resonatorBlockEntity.onRightClick(player);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected void neighborChanged(
        BlockState state,
        Level level,
        BlockPos pos,
        Block neighborBlock,
        BlockPos neighborPos,
        boolean movedByPiston
    ) {
        var hasSignal = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above());
        var triggeredValue = state.getValue(TRIGGERED);
        if (hasSignal && Boolean.TRUE.equals(!triggeredValue)) {
            level.scheduleTick(pos, this, 4);
            level.setBlock(pos, state.setValue(TRIGGERED, Boolean.TRUE), 2);
        } else if (!hasSignal && Boolean.TRUE.equals(triggeredValue)) {
            level.setBlock(pos, state.setValue(TRIGGERED, Boolean.FALSE), 2);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            var blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ResonatorBlockEntity resonatorBlockEntity) {
                resonatorBlockEntity.getResinBallCounts().forEach((resinBallItem, count) -> {
                    if (count > 0) {
                        var resinBallStack = new ItemStack(resinBallItem, count);
                        var resinBallEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, resinBallStack);

                        level.addFreshEntity(resinBallEntity);
                    }
                });
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}
