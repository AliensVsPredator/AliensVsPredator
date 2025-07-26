package com.alien.common.gameplay.block.resin.vent;

import com.alien.common.gameplay.block.entity.resin.vent.ResinVentBlockEntity;
import com.lib.common.gameplay.util.spatial.block.BlockPosUtil;
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

import com.avp.common.registry.init.AVPBlockEntityTypes;

public class ResinVentBlock extends BaseEntityBlock {

    public static final MapCodec<ResinVentBlock> CODEC = simpleCodec(ResinVentBlock::new);

    public ResinVentBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void onRemove(
        @NotNull BlockState blockState,
        @NotNull Level level,
        @NotNull BlockPos blockPos,
        @NotNull BlockState blockState2,
        boolean bl
    ) {
        super.onRemove(blockState, level, blockPos, blockState2, bl);

        if (BlockPosUtil.isFireAdjacent(level, blockPos)) {
            level.setBlock(blockPos, Blocks.BASALT.defaultBlockState(), 3);
        }

        var blockEntity = level.getBlockEntity(blockPos);

        if (blockEntity instanceof ResinVentBlockEntity resinVentBlockEntity) {
            var hive = resinVentBlockEntity.getHive();

            if (hive != null) {
                hive.getVentManager().removeVent(blockPos);
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new ResinVentBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        Level level,
        @NotNull BlockState blockState,
        @NotNull BlockEntityType<T> blockEntityType
    ) {
        return level.isClientSide
            ? null
            : createTickerHelper(blockEntityType, AVPBlockEntityTypes.RESIN_VENT.get(), ResinVentBlockEntity::serverTick);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState blockState) {
        return RenderShape.MODEL;
    }
}
