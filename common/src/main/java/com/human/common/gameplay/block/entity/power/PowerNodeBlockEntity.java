package com.human.common.gameplay.block.entity.power;

import com.human.common.gameplay.power.PowerNode;
import com.human.common.gameplay.power.PowerSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class PowerNodeBlockEntity extends BlockEntity {

    private boolean hasInitialized = false;

    private boolean hasPower;

    private final PowerNode powerNode;

    public PowerNodeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);

        // TODO: Remove this, this is bad practice.
        if (!(this instanceof PowerNode node)) {
            throw new IllegalStateException("Child of PowerNodeBlockEntity must be instance of PowerNode!");
        }

        this.powerNode = node;
    }

    @Override
    public void setRemoved() {
        if (!isRemoved()) {
            var level = getLevel();

            if (level != null && !level.isClientSide) {
                PowerSystem.get((ServerLevel) level).unregisterNode(worldPosition, powerNode);
            }
        }

        super.setRemoved();
    }

    public void setHasInitialized(boolean hasInitialized) {
        this.hasInitialized = hasInitialized;
    }

    public boolean hasInitialized() {
        return hasInitialized;
    }

    public void setHasPower(boolean hasPower) {
        this.hasPower = hasPower;
    }

    public boolean hasPower() {
        return hasPower;
    }
}
