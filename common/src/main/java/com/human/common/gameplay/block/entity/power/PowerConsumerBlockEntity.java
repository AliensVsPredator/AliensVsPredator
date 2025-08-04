package com.human.common.gameplay.block.entity.power;

import com.human.common.gameplay.power.PowerNode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class PowerConsumerBlockEntity extends PowerNodeBlockEntity implements PowerNode.PowerConsumer {

    private boolean hasPower;

    public PowerConsumerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public long receivePower(long maxAmount) {
        setHasPower(true);
        return 0;
    }

    public void setHasPower(boolean hasPower) {
        this.hasPower = hasPower;
    }

    public boolean hasPower() {
        return hasPower;
    }
}
