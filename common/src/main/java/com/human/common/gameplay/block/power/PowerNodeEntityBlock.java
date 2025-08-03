package com.human.common.gameplay.block.power;

import com.human.common.gameplay.block.entity.power.PowerNodeBlockEntity;
import com.human.common.gameplay.power.PowerNode;
import com.human.common.gameplay.power.PowerSystem;
import com.human.common.gameplay.power.grid.PowerGridExploreUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.avp.AVP;

public abstract class PowerNodeEntityBlock<T extends PowerNodeBlockEntity & PowerNode> extends BaseEntityBlock {

    public PowerNodeEntityBlock(Properties properties) {
        super(properties);
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public <U extends BlockEntity> BlockEntityTicker<U> getTicker(
        @NotNull Level level,
        @NotNull BlockState blockState,
        @NotNull BlockEntityType<U> blockEntityType
    ) {
        return level.isClientSide
            ? null
            : createTickerHelper(blockEntityType, (BlockEntityType<T>) blockEntityType, this::serverTick);
    }

    @Override
    protected void onPlace(
        @NotNull BlockState state,
        @NotNull Level level,
        @NotNull BlockPos pos,
        @NotNull BlockState oldState,
        boolean movedByPiston
    ) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        if (level.isClientSide) {
            return;
        }

        var manager = PowerSystem.get((ServerLevel) level);

        for (var direction : Direction.values()) {
            var neighbor = pos.relative(direction);
            var neighborState = level.getBlockState(neighbor);

            // TODO: Use block tags here.
            if (neighborState.getBlock() instanceof CableBlock || level.getBlockEntity(neighbor) instanceof PowerNode) {
                manager.union(pos, neighbor);
            }
        }
    }

    @Override
    protected void onRemove(
        @NotNull BlockState state,
        Level level,
        @NotNull BlockPos pos,
        @NotNull BlockState newState,
        boolean movedByPiston
    ) {
        if (!level.isClientSide && state.getBlock() != newState.getBlock()) {
            PowerSystem.get((ServerLevel) level).splitGrid(level, pos);
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    protected void serverTick(
        Level level,
        BlockPos blockPos,
        BlockState blockState,
        T powerNodeBlockEntity
    ) {
        if (!powerNodeBlockEntity.hasInitialized() && level != null && !level.isClientSide) {
            powerNodeBlockEntity.setHasInitialized(true);

            var serverLevel = (ServerLevel) level;
            var manager = PowerSystem.get(serverLevel);
            manager.registerNode(blockPos, powerNodeBlockEntity);

            AVP.LOGGER.debug("Starting BFS for PNBE at pos {}", blockPos);
            var start = System.currentTimeMillis();
            var connectedPositions = PowerGridExploreUtil.discover(serverLevel, blockPos);
            AVP.LOGGER.debug("Finished BFS for PNBE at pos {} in {}ms", blockPos, System.currentTimeMillis() - start);

            for (var pos : connectedPositions) {
                manager.union(blockPos, pos);
            }
        }
    }
}
