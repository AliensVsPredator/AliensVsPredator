package com.human.common.gameplay.block.entity.power.impl;

import com.human.common.gameplay.block.entity.power.PowerNodeBlockEntity;
import com.human.common.gameplay.power.PowerNode;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;

import com.avp.common.registry.init.AVPBlockEntityTypes;

public class SolarPanelBlockEntity extends PowerNodeBlockEntity implements PowerNode.PowerProducer {

    private static final int MC_DAY_LENGTH_IN_TICKS = 24000;

    private static final int MC_START_OF_DUSK_IN_TICKS = 12000;

    private static final int MC_START_OF_NIGHT_IN_TICKS = 14000;

    private static final int MC_START_OF_DAWN_IN_TICKS = 23000;

    private static final int MC_START_OF_DAY_IN_TICKS = 1000;

    public SolarPanelBlockEntity(BlockPos pos, BlockState state) {
        super(AVPBlockEntityTypes.SOLAR_PANEL.get(), pos, state);
    }

    @Override
    public long getAvailablePower() {
        if (level == null || level.isClientSide) {
            return 0;
        }

        var abovePos = getBlockPos().above();

        // Only generate if the sky is visible.
        if (!level.canSeeSky(abovePos)) {
            return 0;
        }

        // Get the skylight value (0 to 15).
        var skyLight = level.getBrightness(LightLayer.SKY, abovePos);

        if (skyLight == 0) {
            return 0;
        }

        var baseOutput = skyLight * 10;

        // Compute sun intensity based on daytime.
        double sunlightFactor = computeSunlightFactor(level);

        // Reduce power during weather
        var weatherFactor = 1.0;

        if (level.isThundering()) {
            weatherFactor = 0.2;
        } else if (level.isRaining()) {
            weatherFactor = 0.5;
        }

        return (long) (baseOutput * sunlightFactor * weatherFactor);
    }

    private double computeSunlightFactor(Level level) {
        var timeOfDay = level.getDayTime() % MC_DAY_LENGTH_IN_TICKS;

        double sunlightFactor;

        // Clamp curve so it's flat at night
        if (timeOfDay <= MC_START_OF_DAY_IN_TICKS) {
            // Second half of dawn.
            sunlightFactor = Mth.map(timeOfDay, 0, 1000, 0.5, 1);
        } else if (timeOfDay <= MC_START_OF_DUSK_IN_TICKS) {
            // Daylight.
            sunlightFactor = 1.0;
        } else if (timeOfDay <= MC_START_OF_NIGHT_IN_TICKS) {
            // Dusk.
            sunlightFactor = Mth.map(14000 - timeOfDay, 0, 14000 - 12000, 0, 1);
        } else if (timeOfDay <= MC_START_OF_DAWN_IN_TICKS) {
            // Night.
            sunlightFactor = 0;
        } else {
            // First half of dawn.
            sunlightFactor = Mth.map(timeOfDay, 23000, 23999, 0, 0.5);
        }

        return sunlightFactor;
    }

    @Override
    public long extractPower(long maxAmount) {
        // Always gives exactly what was requested.
        return maxAmount;
    }
}
