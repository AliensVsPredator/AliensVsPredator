package org.avp.common.block.power;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.avp.common.block.entity.SolarPanelBlockEntity;
import org.avp.common.block.entity.data.SolarPanelData;
import org.avp.common.block.entity.ticker.SolarPanelBlockTicker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SolarPanelBlock extends BaseEntityBlock {
    public static final MapCodec<SolarPanelBlock> CODEC = simpleCodec(SolarPanelBlock::new);

    public SolarPanelBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new SolarPanelBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        @NotNull Level level,
        @NotNull BlockState blockState,
        @NotNull BlockEntityType<T> blockEntityType
    ) {
        if (!level.isClientSide && level.dimensionType().hasSkyLight()) {
            var solarPanelBlockEntityType = SolarPanelData.INSTANCE.getHolder().get();
            return createTickerHelper(blockEntityType, solarPanelBlockEntityType, SolarPanelBlockTicker.INSTANCE);
        }

        return null;
    }
}
