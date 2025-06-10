package com.predator.common.gameplay.block;

import com.mojang.serialization.MapCodec;
import com.predator.common.gameplay.block.entity.TripMineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.avp.common.registry.init.AVPBlockEntityTypes;

public class TripMineBlock extends BaseEntityBlock {

    public static final MapCodec<TripMineBlock> CODEC = simpleCodec(TripMineBlock::new);

    public TripMineBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new TripMineBlockEntity(blockPos, blockState);
    }

    @Override
    protected @NotNull VoxelShape getShape(
        @NotNull BlockState state,
        @NotNull BlockGetter level,
        @NotNull BlockPos pos,
        @NotNull CollisionContext context
    ) {
        return Block.box(4, 0, 4, 12, 2.2, 11);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        @NotNull Level level,
        @NotNull BlockState blockState,
        @NotNull BlockEntityType<T> blockEntityType
    ) {
        return TripMineBlock.createServerTicker(level, blockEntityType);
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
                (BlockEntityType<? extends TripMineBlockEntity>) AVPBlockEntityTypes.TRIP_MINE.get(),
                TripMineBlockEntity::serverTick
            );
    }

    @Override
    public boolean dropFromExplosion(@NotNull Explosion explosion) {
        return false;
    }
}
