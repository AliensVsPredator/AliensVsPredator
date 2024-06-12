package org.avp.api.common.power;

import net.minecraft.core.BlockPos;

import java.util.Set;

public record PowerNode(
    BlockPos blockPos,
    Set<PowerPath> powerPaths
) {

    public void addConnection(PowerPath powerPath) {
        powerPaths.add(powerPath);
    }
}
