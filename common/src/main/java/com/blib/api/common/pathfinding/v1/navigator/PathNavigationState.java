package com.blib.api.common.pathfinding.v1.navigator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import com.blib.api.common.pathfinding.v1.debug.PathSearchSnapshot;
import com.blib.api.common.pathfinding.v1.node.PathNode;
import com.blib.api.common.pathfinding.v1.path.BLibPath;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * Read-only view of the active BLib path navigation state.
 */
public interface PathNavigationState {

    boolean isPathPending();

    int getTickCount();

    int getLastPathComputeTick();

    int getLastProgressTick();

    long getLastPathComputeNanos();

    @Nullable PathSearchSnapshot getLastSearchSnapshot();

    @Nullable PathNode getCurrentNode();

    @Nullable BlockPos getCurrentTargetPos();

    @Nullable Vec3 getCurrentTargetCenter();

    boolean isNavigating();

    boolean isDone();

    @Nullable BLibPath getCurrentPath();

    @Nullable TerrainType getCurrentTerrain();

    boolean canOpenDoors();

    @Nullable BlockPos getTargetPos();

    int getConsecutiveFailures();

    int getFailureCooldownRemainingTicks();
}
