package com.avp.core.common.block.impl;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.avp.core.common.block.entity.BlockEntityTypes;
import com.avp.core.common.block.entity.resin_node.ResinNodeBlockEntity;
import com.avp.core.common.util.BlockPosUtil;

public class ResinNodeBlock extends BaseEntityBlock {

    public static final MapCodec<ResinNodeBlock> CODEC = simpleCodec(ResinNodeBlock::new);

    public ResinNodeBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void onRemove(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull BlockState blockState2, boolean bl) {
        super.onRemove(blockState, level, blockPos, blockState2, bl);

        if (BlockPosUtil.isFireAdjacent(level, blockPos)) {
            level.setBlock(blockPos, Blocks.BASALT.defaultBlockState(), 3);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new ResinNodeBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState blockState, @NotNull BlockEntityType<T> blockEntityType) {
        return level.isClientSide
            ? null
            : createTickerHelper(blockEntityType, BlockEntityTypes.RESIN_NODE.get(), ResinNodeBlockEntity::serverTick);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState blockState) {
        return RenderShape.MODEL;
    }
}
