package com.human.common.gameplay.block.entity.power.impl;

import com.human.common.gameplay.block.entity.power.PowerNodeBlockEntity;
import com.human.common.gameplay.power.PowerNode;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import com.avp.common.registry.init.AVPBlockEntityTypes;

public class WindTurbineBlockEntity extends PowerNodeBlockEntity implements PowerNode.PowerProducer {

    public WindTurbineBlockEntity(BlockPos pos, BlockState state) {
        super(AVPBlockEntityTypes.WIND_TURBINE.get(), pos, state);
    }

    @Override
    public long getAvailablePower() {
        if (level == null || level.isClientSide) {
            return 0;
        }

        var blockPos = getBlockPos();
        var abovePos = blockPos.above();

        // Only generate if the sky is visible.
        if (!level.canSeeSky(abovePos)) {
            return 0;
        }

        // Uses y value directly for the base output. This means that at sea level, wind turbines will output 65p.
        var baseOutput = blockPos.getY();

        // Increase power during weather (more wind).
        var biomeFactor = computeBiomeFactor(level, blockPos);
        // Increase power during weather (more wind).
        var weatherFactor = computeWeatherFactor(level);

        return (long) (baseOutput * biomeFactor * weatherFactor);
    }

    private double computeBiomeFactor(Level level, BlockPos blockPos) {
        var biome = level.getBiome(blockPos);

        if (biome.is(BiomeTags.IS_MOUNTAIN)) {
            return 2.0;
        } else if (biome.is(BiomeTags.IS_HILL)) {
            return 1.75;
        } else if (biome.is(BiomeTags.IS_OCEAN)) {
            return 1.5;
        } else if (biome.is(BiomeTags.IS_BEACH)) {
            return 1.25;
        }

        return 1.0;
    }

    private double computeWeatherFactor(Level level) {
        if (level.isThundering()) {
            return 2.0;
        } else if (level.isRaining()) {
            return 1.5;
        }

        return 1.0;
    }

    @Override
    public long extractPower(long maxAmount) {
        // Always gives exactly what was requested.
        return maxAmount;
    }
}
