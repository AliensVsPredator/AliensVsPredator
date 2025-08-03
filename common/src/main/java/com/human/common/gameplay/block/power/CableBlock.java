package com.human.common.gameplay.block.power;

import com.human.common.gameplay.power.PowerNode;
import com.human.common.gameplay.power.PowerSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CableBlock extends Block {

    public CableBlock(Properties properties) {
        super(properties);
    }

    // TODO: Deduplicate this code.
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

    // TODO: Deduplicate this code.
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
}
