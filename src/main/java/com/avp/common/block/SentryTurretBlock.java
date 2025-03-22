package com.avp.common.block;

import com.avp.common.block.base.BaseBlockEntity;
import com.avp.common.block.entity.BlockEntityTypes;
import com.avp.common.block.entity.SentryTurretBE;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SentryTurretBlock extends BaseBlockEntity {

    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    public static final MapCodec<SentryTurretBlock> CODEC = simpleCodec(SentryTurretBlock::new);

    protected SentryTurretBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TRIGGERED, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TRIGGERED);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SentryTurretBE(blockPos, blockState);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return SentryTurretBlock.createSentryTicker(level, blockEntityType);
    }

    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createSentryTicker(
            Level level,
            BlockEntityType<T> blockEntityType
    ) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, BlockEntityTypes.SENTRY_TURRET_BE, SentryTurretBE::serverTick);
    }

    protected void openContainer(Level level, BlockPos blockPos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof SentryTurretBE) {
            player.openMenu((MenuProvider) (blockEntity));
        }
    }

    @Override
    protected boolean isTickingBE() {
        return true;
    }

    @Override
    public boolean dropFromExplosion(Explosion explosion) {
        return false;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        var hasSignal = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above());
        var triggeredValue = state.getValue(TRIGGERED);
        if (hasSignal && Boolean.TRUE.equals(!triggeredValue)) {
            level.scheduleTick(pos, this, 4);
            level.setBlock(pos, state.setValue(TRIGGERED, Boolean.TRUE), 2);
        } else if (!hasSignal && Boolean.TRUE.equals(triggeredValue)) {
            level.setBlock(pos, state.setValue(TRIGGERED, Boolean.FALSE), 2);
        }
    }
}
