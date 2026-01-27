package com.blib.mod.common.gameplay.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.function.ToIntFunction;

import com.blib.mod.common.gameplay.block.entity.TickingLightBlockEntity;
import com.blib.mod.common.registry.init.BLibBlockEntityTypes;

public class TickingLightBlock extends BaseEntityBlock {

    public static final MapCodec<TickingLightBlock> CODEC = simpleCodec($ -> new TickingLightBlock());

    public static final IntegerProperty LIGHT_LEVEL = BlockStateProperties.LEVEL;

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final ToIntFunction<BlockState> LIGHT_EMISSION = state -> state.getValue(LIGHT_LEVEL);

    public TickingLightBlock() {
        super(
            BlockBehaviour.Properties.of()
                .sound(SoundType.CANDLE)
                .lightLevel(TickingLightBlock.LIGHT_EMISSION)
                .pushReaction(PushReaction.DESTROY)
                .noLootTable()
                .noCollission()
                .replaceable()
                .noOcclusion()
        );

        this.registerDefaultState(
            this.stateDefinition.any().setValue(LIGHT_LEVEL, 15).setValue(WATERLOGGED, Boolean.FALSE)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIGHT_LEVEL, WATERLOGGED);
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TickingLightBlockEntity(pos, state);
    }

    @Override
    public @NotNull VoxelShape getShape(
        @NotNull BlockState blockState,
        @NotNull BlockGetter blockGetter,
        @NotNull BlockPos blockPos,
        @NotNull CollisionContext collisionContext
    ) {
        return Shapes.empty();
    }

    @Override
    public boolean propagatesSkylightDown(
        @NotNull BlockState state,
        @NotNull BlockGetter world,
        @NotNull BlockPos pos
    ) {
        return state.getFluidState().isEmpty();
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected float getShadeBrightness(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return 1.0F;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        @NotNull Level world,
        @NotNull BlockState state,
        @NotNull BlockEntityType<T> type
    ) {
        return createTickerHelper(type, BLibBlockEntityTypes.TICKING_LIGHT.get(), TickingLightBlockEntity::tick);
    }

    @Override
    protected @NotNull BlockState updateShape(
        BlockState state,
        @NotNull Direction direction,
        @NotNull BlockState neighborState,
        @NotNull LevelAccessor level,
        @NotNull BlockPos pos,
        @NotNull BlockPos neighborPos
    ) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED)
            ? Fluids.WATER.getSource(false)
            : super.getFluidState(state);
    }

}
