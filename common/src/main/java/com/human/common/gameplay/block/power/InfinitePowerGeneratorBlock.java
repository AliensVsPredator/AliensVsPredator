package com.human.common.gameplay.block.power;

import com.human.common.gameplay.block.entity.power.impl.InfinitePowerGeneratorBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InfinitePowerGeneratorBlock extends PowerNodeEntityBlock<InfinitePowerGeneratorBlockEntity> {

    public static final MapCodec<InfinitePowerGeneratorBlock> CODEC = simpleCodec(InfinitePowerGeneratorBlock::new);

    public InfinitePowerGeneratorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new InfinitePowerGeneratorBlockEntity(blockPos, blockState);
    }
}
