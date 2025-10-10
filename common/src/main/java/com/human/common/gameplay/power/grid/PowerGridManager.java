package com.human.common.gameplay.power.grid;

import com.human.common.gameplay.power.PowerNode;
import com.lib.common.util.DirectionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class PowerGridManager {

    private final PowerGridDSU dsu;

    // Maps root BlockPos to the actual PowerGrid.
    private final Map<BlockPos, PowerGrid> grids;

    public PowerGridManager() {
        this.dsu = new PowerGridDSU();
        this.grids = new HashMap<>();
    }

    /**
     * Called once per tick from the world or mod tick handler.
     */
    public void tick() {
        for (var grid : grids.values()) {
            grid.tick();
        }
    }

    /**
     * Merges the grids of two positions (e.g., when a new cable connects them).
     */
    public void union(BlockPos a, BlockPos b) {
        var rootA = dsu.find(a);
        var rootB = dsu.find(b);

        if (rootA.equals(rootB)) {
            // Roots are the same, nothing to union here.
            return;
        }

        var gridA = grids.computeIfAbsent(rootA, r -> new PowerGrid());
        var gridB = grids.computeIfAbsent(rootB, r -> new PowerGrid());

        gridA.getProducers().forEach(gridB::add);
        gridA.getConsumers().forEach(gridB::add);
        gridA.getStores().forEach(gridB::add);

        // drop old grid.
        grids.remove(rootA);
        // merge.
        dsu.setParent(rootA, rootB);
    }

    public void splitGrid(Level level, BlockPos removedPos) {
        var oldRoot = dsu.find(removedPos);

        dsu.remove(removedPos);

        // Remove the old PowerGrid (if it exists).
        grids.remove(oldRoot);

        var visited = new HashSet<BlockPos>();

        for (var direction : DirectionUtil.VALUES) {
            var neighbor = removedPos.relative(direction);

            if (!visited.contains(neighbor) && PowerGridExploreUtil.isConnectable(level, neighbor)) {
                // BFS from this neighbor.
                var component = PowerGridExploreUtil.discover(level, neighbor);

                // Pick new root (e.g., first block).
                var newRoot = component.iterator().next();

                // Create new PowerGrid.
                var grid = new PowerGrid();
                grids.put(newRoot, grid);

                // Union everything into the new root.
                for (var pos : component) {
                    dsu.setParent(pos, newRoot);
                    visited.add(pos);

                    var blockEntity = level.getBlockEntity(pos);

                    if (blockEntity instanceof PowerNode node) {
                        grid.add(node);
                    }
                }
            }
        }
    }

    /**
     * Registers a node (block entity) into the grid at the given position.
     */
    public void registerNode(BlockPos pos, PowerNode node) {
        var root = dsu.find(pos);
        grids.computeIfAbsent(root, r -> new PowerGrid()).add(node);
    }

    /**
     * Removes a node from its grid (e.g., block broken).
     */
    public void unregisterNode(BlockPos pos, PowerNode node) {
        var root = dsu.find(pos);
        var grid = grids.get(root);

        if (grid != null) {
            grid.remove(node);
        }
    }
}
