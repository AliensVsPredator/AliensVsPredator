package org.avp.common.game.block_entity.ticker;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import org.avp.common.game.block_entity.SolarPanelBlockEntity;

public class SolarPanelBlockTicker implements BlockEntityTicker<SolarPanelBlockEntity> {

    public static final SolarPanelBlockTicker INSTANCE = new SolarPanelBlockTicker();

    private SolarPanelBlockTicker() {}

    @Override
    public void tick(
        @NotNull Level level,
        @NotNull BlockPos blockPos,
        @NotNull BlockState blockState,
        @NotNull SolarPanelBlockEntity solarPanelBlockEntity
    ) {
        // If the sky is dark during rain or thunder, the solar panel can not provide power.
        if (level.getSkyDarken() > 0) {
            return;
        }

        int i = level.getBrightness(LightLayer.SKY, blockPos);

        // TODO: Provide power to consumers.
        // this.getPowerGraph().providePower(i);
    }
}
