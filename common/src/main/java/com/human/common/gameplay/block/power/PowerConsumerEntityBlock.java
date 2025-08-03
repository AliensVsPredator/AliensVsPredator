package com.human.common.gameplay.block.power;

import com.human.common.gameplay.block.entity.power.PowerNodeBlockEntity;
import com.human.common.gameplay.power.PowerNode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class PowerConsumerEntityBlock<T extends PowerNodeBlockEntity & PowerNode.PowerConsumer> extends PowerNodeEntityBlock<T> {

    public PowerConsumerEntityBlock(Properties properties) {
        super(properties);
    }

    public abstract void unpoweredTick(
        Level level,
        BlockPos blockPos,
        BlockState blockState,
        T powerNodeBlockEntity
    );

    public abstract void poweredTick(
        Level level,
        BlockPos blockPos,
        BlockState blockState,
        T powerNodeBlockEntity
    );

    @Override
    protected void serverTick(Level level, BlockPos blockPos, BlockState blockState, T powerNodeBlockEntity) {
        super.serverTick(level, blockPos, blockState, powerNodeBlockEntity);

        if (!powerNodeBlockEntity.hasPower()) {
            // Not enough power to operate.
            unpoweredTick(level, blockPos, blockState, powerNodeBlockEntity);
            return;
        }

        poweredTick(level, blockPos, blockState, powerNodeBlockEntity);

        powerNodeBlockEntity.setHasPower(false);
    }
}
