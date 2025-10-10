package com.human.common.gameplay.power.grid;

import com.human.common.gameplay.power.PowerNode;
import com.just.core.traversal.BFS;
import com.lib.common.util.DirectionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.*;

import com.avp.common.registry.init.block.AVPBlocks;

public class PowerGridExploreUtil {

    /**
     * Performs a BFS traversal to find all physically connected cable blocks and power nodes.
     *
     * @param start The position to begin traversal.
     * @return A set of all positions in the same network component.
     */
    public static Set<BlockPos> discover(Level level, BlockPos start) {
        var visited = new HashSet<BlockPos>();

        BFS.traverse(
            start,
            current -> Arrays.stream(DirectionUtil.VALUES)
                .map(current::relative)
                .filter(relPos -> isConnectable(level, relPos))
                .toList(),
            visited::add
        );

        return visited;
    }

    /**
     * Determines if a connection exists between two adjacent positions.
     */
    public static boolean isConnectable(Level level, BlockPos blockPos) {
        var toState = level.getBlockState(blockPos);

        // Check if the destination block is a cable or power node.
        var toBlock = toState.getBlock();

        if (toBlock == AVPBlocks.CABLE.get()) {
            return true;
        }

        return level.getBlockEntity(blockPos) instanceof PowerNode;
    }
}
