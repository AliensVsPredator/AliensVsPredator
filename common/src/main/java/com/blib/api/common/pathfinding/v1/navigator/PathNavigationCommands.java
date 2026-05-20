package com.blib.api.common.pathfinding.v1.navigator;

import net.minecraft.core.BlockPos;

import java.util.Objects;

import com.blib.api.common.pathfinding.v1.feature.PathfindingFeatures;

/**
 * Commands that start, update, tick, or stop an active BLib path navigation request.
 */
public interface PathNavigationCommands {

    /**
     * Returns the coordinate-to-anchor resolver used by coordinate-based navigation overloads.
     */
    PathNavigationAnchorResolver getAnchorResolver();

    boolean navigateTo(BlockPos entityPos, BlockPos target);

    boolean navigateTo(BlockPos entityPos, BlockPos target, PathfindingFeatures pathfindingFeatures);

    default boolean navigateTo(double entityX, double entityY, double entityZ, BlockPos target) {
        var anchorResolver = getAnchorResolver();

        return navigateTo(anchorResolver.entityAnchorPos(entityX, entityY, entityZ), target);
    }

    default boolean navigateTo(
        double entityX,
        double entityY,
        double entityZ,
        BlockPos target,
        PathfindingFeatures pathfindingFeatures
    ) {
        var anchorResolver = getAnchorResolver();

        return navigateTo(
            anchorResolver.entityAnchorPos(entityX, entityY, entityZ),
            target,
            Objects.requireNonNull(pathfindingFeatures, "pathfindingFeatures")
        );
    }

    default boolean navigateTo(
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

    default boolean navigateTo(
        double entityX,
        double entityY,
        double entityZ,
        double targetX,
        double targetY,
        double targetZ,
        PathfindingFeatures pathfindingFeatures
    ) {
        var anchorResolver = getAnchorResolver();

        return navigateTo(
            anchorResolver.entityAnchorPos(entityX, entityY, entityZ),
            anchorResolver.targetAnchorPos(targetX, targetY, targetZ),
            Objects.requireNonNull(pathfindingFeatures, "pathfindingFeatures")
        );
    }

    void navigateToAsync(BlockPos entityPos, BlockPos rawTarget);

    void navigateToAsync(BlockPos entityPos, BlockPos rawTarget, PathfindingFeatures pathfindingFeatures);

    default void navigateToAsync(double entityX, double entityY, double entityZ, BlockPos target) {
        var anchorResolver = getAnchorResolver();

        navigateToAsync(anchorResolver.entityAnchorPos(entityX, entityY, entityZ), target);
    }

    default void navigateToAsync(
        double entityX,
        double entityY,
        double entityZ,
        BlockPos target,
        PathfindingFeatures pathfindingFeatures
    ) {
        var anchorResolver = getAnchorResolver();

        navigateToAsync(
            anchorResolver.entityAnchorPos(entityX, entityY, entityZ),
            target,
            Objects.requireNonNull(pathfindingFeatures, "pathfindingFeatures")
        );
    }

    default void navigateToAsync(
        double entityX,
        double entityY,
        double entityZ,
        double targetX,
        double targetY,
        double targetZ
    ) {
        var anchorResolver = getAnchorResolver();

        navigateToAsync(
            anchorResolver.entityAnchorPos(entityX, entityY, entityZ),
            anchorResolver.targetAnchorPos(targetX, targetY, targetZ)
        );
    }

    default void navigateToAsync(
        double entityX,
        double entityY,
        double entityZ,
        double targetX,
        double targetY,
        double targetZ,
        PathfindingFeatures pathfindingFeatures
    ) {
        var anchorResolver = getAnchorResolver();

        navigateToAsync(
            anchorResolver.entityAnchorPos(entityX, entityY, entityZ),
            anchorResolver.targetAnchorPos(targetX, targetY, targetZ),
            Objects.requireNonNull(pathfindingFeatures, "pathfindingFeatures")
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
