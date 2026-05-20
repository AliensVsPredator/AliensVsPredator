package com.blib.api.common.pathfinding.v1.navigator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import com.blib.api.common.pathfinding.v1.debug.PathSearchSnapshot;
import com.blib.api.common.pathfinding.v1.node.PathNode;
import com.blib.api.common.pathfinding.v1.path.BLibPath;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * Mutable state backing a BLib path navigation request, exposed externally as a read-only {@link PathNavigationState}.
 */
public final class PathNavigationStateComponent implements PathNavigationState {

    private final Supplier<@Nullable PathSearchSnapshot> lastSearchSnapshotSupplier;

    private final Supplier<@Nullable Vec3> currentTargetCenterSupplier;

    private final BooleanSupplier canOpenDoorsSupplier;

    private final LongSupplier cooldownClockSupplier;

    @Nullable BLibPath currentPath;

    @Nullable BlockPos rawTargetPos;

    @Nullable BlockPos targetPos;

    @Nullable TerrainType currentTerrain;

    int tickCount;

    int lastPathComputeTick;

    int lastProgressTick;

    long lastPathComputeNanos;

    @Nullable CompletableFuture<@Nullable BLibPath> pendingPath;

    int consecutiveFailures;

    int failureCooldownTicks;

    long lastFailureTick;

    double lastEntityX;

    double lastEntityY;

    double lastEntityZ;

    boolean hasLastEntityPosition;

    float lastEntityWidth = 1.0f;

    float lastEntityHeight = 2.0f;

    PathNavigationStateComponent(
        Supplier<@Nullable PathSearchSnapshot> lastSearchSnapshotSupplier,
        Supplier<@Nullable Vec3> currentTargetCenterSupplier,
        BooleanSupplier canOpenDoorsSupplier,
        LongSupplier cooldownClockSupplier
    ) {
        this.lastSearchSnapshotSupplier = lastSearchSnapshotSupplier;
        this.currentTargetCenterSupplier = currentTargetCenterSupplier;
        this.canOpenDoorsSupplier = canOpenDoorsSupplier;
        this.cooldownClockSupplier = cooldownClockSupplier;
    }

    @Override
    public boolean isPathPending() {
        return pendingPath != null;
    }

    @Override
    public int getTickCount() {
        return tickCount;
    }

    @Override
    public int getLastPathComputeTick() {
        return lastPathComputeTick;
    }

    @Override
    public int getLastProgressTick() {
        return lastProgressTick;
    }

    @Override
    public long getLastPathComputeNanos() {
        return lastPathComputeNanos;
    }

    @Override
    public @Nullable PathSearchSnapshot getLastSearchSnapshot() {
        return lastSearchSnapshotSupplier.get();
    }

    @Override
    public @Nullable PathNode getCurrentNode() {
        if (currentPath == null || currentPath.isDone()) {
            return null;
        }

        return currentPath.getCurrentNode();
    }

    @Override
    public @Nullable BlockPos getCurrentTargetPos() {
        var node = getCurrentNode();

        if (node == null) {
            return null;
        }

        return new BlockPos(node.getX(), node.getY(), node.getZ());
    }

    @Override
    public @Nullable Vec3 getCurrentTargetCenter() {
        if (currentPath == null || currentPath.isDone()) {
            return null;
        }

        return currentTargetCenterSupplier.get();
    }

    @Override
    public boolean isNavigating() {
        return currentPath != null && !currentPath.isDone();
    }

    @Override
    public boolean isDone() {
        return currentPath == null || currentPath.isDone();
    }

    @Override
    public @Nullable BLibPath getCurrentPath() {
        return currentPath;
    }

    @Override
    public @Nullable TerrainType getCurrentTerrain() {
        return currentTerrain;
    }

    @Override
    public boolean canOpenDoors() {
        return canOpenDoorsSupplier.getAsBoolean();
    }

    @Override
    public @Nullable BlockPos getTargetPos() {
        return targetPos;
    }

    @Override
    public int getConsecutiveFailures() {
        return consecutiveFailures;
    }

    @Override
    public int getFailureCooldownRemainingTicks() {
        if (consecutiveFailures == 0) {
            return 0;
        }

        return (int) Math.max(0, failureCooldownTicks - (cooldownClockSupplier.getAsLong() - lastFailureTick));
    }
}
