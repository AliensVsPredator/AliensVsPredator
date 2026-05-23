package com.blib.api.common.pathfinding.v1.navigator;

import net.minecraft.core.BlockPos;

/**
 * Converts entity and target center coordinates into pathfinding footprint anchors.
 */
public interface PathNavigationAnchorResolver {

    BlockPos entityAnchorPos(double entityX, double entityY, double entityZ);

    BlockPos targetAnchorPos(double targetX, double targetY, double targetZ);
}
