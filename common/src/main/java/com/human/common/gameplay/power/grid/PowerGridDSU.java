package com.human.common.gameplay.power.grid;

import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class PowerGridDSU {

    // Maps each block position to the root of its grid (used for DSU)
    private final Map<BlockPos, BlockPos> parentMap;

    public PowerGridDSU() {
        this.parentMap = new HashMap<>();
    }

    /**
     * Finds the root BlockPos for the grid that contains the given position.
     */
    public BlockPos find(BlockPos pos) {
        var parent = parentMap.get(pos);

        if (parent == null) {
            parentMap.put(pos, pos);
            return pos;
        }

        if (!parent.equals(pos)) {
            // path compression.
            var root = find(parent);
            parentMap.put(pos, root);
            return root;
        }

        return parent;
    }

    public void remove(BlockPos pos) {
        parentMap.remove(pos);
    }

    public void setParent(BlockPos rootA, BlockPos rootB) {
        parentMap.put(rootA, rootB);
    }
}
