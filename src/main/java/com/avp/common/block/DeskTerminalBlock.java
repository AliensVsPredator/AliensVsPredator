package com.avp.common.block;

import com.avp.common.block.base.BaseBlockEntity;
import com.avp.common.block.entity.DeskTerminalBE;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DeskTerminalBlock extends BaseBlockEntity {

    public static final MapCodec<DeskTerminalBlock> CODEC = simpleCodec(DeskTerminalBlock::new);

    public DeskTerminalBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new DeskTerminalBE(blockPos, blockState);
    }

    @Override
    protected boolean isTickingBE() {
        return false;
    }
}
