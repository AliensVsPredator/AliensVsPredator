package com.human.common.gameplay.block.entity.power.impl;

import com.human.common.gameplay.block.entity.power.PowerConsumerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import com.avp.common.registry.init.AVPBlockEntityTypes;

public class DeskTerminalBlockEntity extends PowerConsumerBlockEntity {

    public DeskTerminalBlockEntity(BlockPos pos, BlockState blockState) {
        super(AVPBlockEntityTypes.DESK_TERMINAL.get(), pos, blockState);
    }

    @Override
    public void unpoweredTick(Level level, BlockPos blockPos, BlockState blockState) {}

    @Override
    public void poweredTick(Level level, BlockPos blockPos, BlockState blockState) {}

    @Override
    public long getRequestedPower() {
        return 1000;
    }
}
