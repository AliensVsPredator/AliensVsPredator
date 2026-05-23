package com.blib.api.common.pathfinding.v1.navigator;

import net.minecraft.core.BlockPos;

/**
 * Commands that start, update, tick, or stop an active BLib path navigation request.
 */
public interface PathNavigationCommands {

    /**
     * Returns the coordinate-to-anchor resolver used by coordinate-based navigation overloads.
     */
    PathNavigationAnchorResolver getAnchorResolver();

    PathNavigationRequest navigateTo(BlockPos entityPos, BlockPos target);

    default PathNavigationRequest navigateTo(double entityX, double entityY, double entityZ, BlockPos target) {
        var anchorResolver = getAnchorResolver();

        return navigateTo(anchorResolver.entityAnchorPos(entityX, entityY, entityZ), target);
    }

    default PathNavigationRequest navigateTo(
        double entityX,
        double entityY,
        double entityZ,
        double targetX,
        double targetY,
        double targetZ
    ) {
        var anchorResolver = getAnchorResolver();

        return navigateTo(
            anchorResolver.entityAnchorPos(entityX, entityY, entityZ),
            anchorResolver.targetAnchorPos(targetX, targetY, targetZ)
        );
    }

    void tick(double entityX, double entityY, double entityZ, float entityWidth, float entityHeight);

    void updateTarget(BlockPos newRawTarget);

    default void updateTarget(double targetX, double targetY, double targetZ) {
        var anchorResolver = getAnchorResolver();

        updateTarget(anchorResolver.targetAnchorPos(targetX, targetY, targetZ));
    }

    void requestReplan();

    void stop();
}
