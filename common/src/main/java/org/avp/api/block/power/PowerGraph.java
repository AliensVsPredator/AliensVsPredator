package org.avp.api.block.power;

import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PowerGraph {

    private final Map<BlockPos, PowerNode> powerNodesByPositionMap;

    public PowerGraph() {
        this.powerNodesByPositionMap = new HashMap<>();
    }

    public void addNode(PowerNode powerNode) {
        powerNodesByPositionMap.put(powerNode.blockPos(), powerNode);
    }

    public void removeNode(BlockPos blockPos) {
        powerNodesByPositionMap.remove(blockPos);
    }

    public Optional<PowerNode> getNode(BlockPos blockPos) {
        return Optional.ofNullable(powerNodesByPositionMap.get(blockPos));
    }

    public void addConnection(PowerPath powerPath) {
        var start = powerPath.start();
        var end = powerPath.end();
        var startNode = getNode(start);
        var endNode = getNode(end);

        if (startNode.isPresent() && endNode.isPresent()) {
            startNode.get().addConnection(powerPath);
            endNode.get().addConnection(powerPath);
        }
    }
}
