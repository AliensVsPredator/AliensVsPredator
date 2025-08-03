package com.human.common.gameplay.block.entity.power;

import com.human.common.gameplay.power.PowerNode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import com.avp.common.registry.init.AVPBlockEntityTypes;

public class InfinitePowerGeneratorBlockEntity extends PowerNodeBlockEntity implements PowerNode.PowerProducer {

    private static final int INFINITE_POWER = 1_000_000_000;

    public InfinitePowerGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(AVPBlockEntityTypes.INFINITE_POWER_GENERATOR.get(), pos, state);
    }

    @Override
    public int getAvailablePower() {
        return INFINITE_POWER;
    }

    @Override
    public int extractPower(int maxAmount) {
        // Always gives exactly what was requested.
        return maxAmount;
    }
}
