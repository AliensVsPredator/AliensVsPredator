package com.avp.common.block.base;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.avp.common.block.entity.base.BaseTickingBE;

public abstract class BaseBlockEntity extends BaseEntityBlock {

    protected BaseBlockEntity(Properties properties) {
        super(properties);
    }

    @Override
    protected abstract @NotNull MapCodec<? extends BaseEntityBlock> codec();

    @Override
    public abstract @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState);

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        Level level,
        BlockState blockState,
        BlockEntityType<T> blockEntityType
    ) {
        return isTickingBE() ? (lvl, pos, state, be) -> {
            if (be instanceof BaseTickingBE tickingBE) {
                tickingBE.tick(lvl, pos, state);
            }
        } : super.getTicker(level, blockState, blockEntityType);
    }

    protected abstract boolean isTickingBE();
}
