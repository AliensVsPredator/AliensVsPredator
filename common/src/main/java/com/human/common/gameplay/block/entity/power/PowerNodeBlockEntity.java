package com.human.common.gameplay.block.entity.power;

import com.human.common.gameplay.power.PowerNode;
import com.human.common.gameplay.power.PowerSystem;
import com.human.common.gameplay.power.grid.PowerGridExploreUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import com.avp.AVP;

public abstract class PowerNodeBlockEntity extends BlockEntity {

    private boolean hasInitialized = false;

    private final PowerNode powerNode;

    public PowerNodeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);

        // TODO: Remove this, this is bad practice.
        if (!(this instanceof PowerNode node)) {
            throw new IllegalStateException("Child of PowerNodeBlockEntity must be instance of PowerNode!");
        }

        this.powerNode = node;
    }

    public void serverTick(Level level, BlockPos blockPos, BlockState blockState) {
        if (!hasInitialized() && level != null && !level.isClientSide) {
            setHasInitialized(true);

            var serverLevel = (ServerLevel) level;
            var manager = PowerSystem.get(serverLevel);
            manager.registerNode(blockPos, powerNode);

            AVP.LOGGER.debug("Starting BFS for PNBE at pos {}", blockPos);
            var start = System.currentTimeMillis();
            var connectedPositions = PowerGridExploreUtil.discover(serverLevel, blockPos);
            AVP.LOGGER.debug("Finished BFS for PNBE at pos {} in {}ms", blockPos, System.currentTimeMillis() - start);

            for (var pos : connectedPositions) {
                manager.union(blockPos, pos);
            }
        }
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
}
