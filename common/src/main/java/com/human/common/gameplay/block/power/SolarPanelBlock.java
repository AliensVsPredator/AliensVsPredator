package com.human.common.gameplay.block.power;

import com.human.common.gameplay.block.entity.power.impl.SolarPanelBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SolarPanelBlock extends PowerNodeEntityBlock<SolarPanelBlockEntity> {

    public static final MapCodec<SolarPanelBlock> CODEC = simpleCodec(SolarPanelBlock::new);

    public SolarPanelBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new SolarPanelBlockEntity(blockPos, blockState);
    }
}
