package com.human.common.gameplay.block.entity.power;

import com.human.common.gameplay.power.PowerNode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class PowerConsumerBlockEntity extends PowerNodeBlockEntity implements PowerNode.PowerConsumer {

    private boolean hasPower;

    public PowerConsumerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public abstract void unpoweredTick(
        Level level,
        BlockPos blockPos,
        BlockState blockState
    );

    public abstract void poweredTick(
        Level level,
        BlockPos blockPos,
        BlockState blockState
    );

    @Override
    public void serverTick(Level level, BlockPos blockPos, BlockState blockState) {
        super.serverTick(level, blockPos, blockState);

        if (!hasPower) {
            // Not enough power to operate.
            unpoweredTick(level, blockPos, blockState);
            return;
        }

        poweredTick(level, blockPos, blockState);

        this.hasPower = false;
    }

    @Override
    public long receivePower(long maxAmount) {
        this.hasPower = maxAmount >= getRequestedPower();
        return 0;
    }
}
