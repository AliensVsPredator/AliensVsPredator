package com.blib.api.common.goap.v1.action.impl;

import com.just.ai.goap.action.Action;
import com.just.ai.goap.StateKey;
import com.just.ai.goap.state.Blackboard;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.phys.Vec3;

import com.blib.api.common.entity.v1.EntityUtil;
import com.blib.api.common.pathfinding.v1.breaking.PathBlockBreakExecutor;
import com.blib.api.common.pathfinding.v1.debug.PathDebugUtil;
import com.blib.api.common.pathfinding.v1.feature.PathfindingFeature;
import com.blib.api.common.pathfinding.v1.movement.PathMovementController;
import com.blib.api.common.pathfinding.v1.navigator.PathNavigatorApi;
import com.blib.api.common.pathfinding.v1.navigator.PathNavigatorUser;

/**
 * GOAP action utility for pathfinding using BLib's {@link PathNavigatorApi}. Requires the entity to implement
 * {@link PathNavigatorUser}.
 * <p>
 * Unlike {@link MoveToPosAction} which delegates to Minecraft's vanilla PathNavigation, this action uses BLib's
 * standalone pathfinding system with multi-terrain support.
 * </p>
 */
public final class NeoMoveToPosAction {

    private static final StateKey<BlockPos> LAST_OPENED_DOOR_POS = StateKey.sensed("neo_last_opened_door_pos");

    private static final StateKey<PathBlockBreakExecutor> BLOCK_BREAK_EXECUTOR = StateKey.sensed(
        "neo_block_break_executor"
    );

    /**
     * Result of a movement tick.
     */
    public enum Result {
        FINISHED,
        MOVING,
        NO_PATH
    }

    /**
     * Performs one tick of pathfinding toward the target position. Plans a path on first call, then follows it on
     * subsequent ticks.
     *
     * @param context         the GOAP action context
     * @param targetPos       the position to navigate to
     * @param speedMultiplier movement speed multiplier
     * @return the result of this tick
     */
    public static Result perform(
        Action.Context<? extends PathfinderMob> context,
        Vec3 targetPos,
        double speedMultiplier
    ) {
        return perform(context, targetPos, speedMultiplier, false);
    }

    /**
     * Performs one tick of pathfinding toward the target position with optional block-breaking path search enabled for
     * this request.
     *
     * @param context            the GOAP action context
     * @param targetPos          the position to navigate to
     * @param speedMultiplier    movement speed multiplier
     * @param allowBlockBreaking whether this path request may plan block-breaking edges
     * @return the result of this tick
     */
    public static Result perform(
        Action.Context<? extends PathfinderMob> context,
        Vec3 targetPos,
        double speedMultiplier,
        boolean allowBlockBreaking
    ) {
        var actor = context.getActor();
        var blackboard = context.getBlackboard(Blackboard.Scope.ACTION);

        if (!(actor instanceof PathNavigatorUser navigatorUser)) {
            return Result.NO_PATH;
        }

        var navigator = navigatorUser.getPathNavigator();

        try {
            var featureControl = navigator.getFeatureControl();
            var requestedFeatures = featureControl.getDefaultPathfindingFeatures()
                .with(PathfindingFeature.BLOCK_BREAKING, allowBlockBreaking);
            var navigatorState = navigator.getState();

            if (
                navigatorState.isNavigating()
                    && !featureControl.getPathfindingFeatures().equals(requestedFeatures)
            ) {
                resetBlockBreakExecutor(actor, blackboard);
                navigator.stop();
            }

            featureControl.setDebugCaptureEnabled(PathDebugUtil.hasDebugWatchers(actor));

            if (!navigatorState.isNavigating()) {
                var pathResult = navigator.navigateTo(
                    actor.getX(),
                    actor.getY(),
                    actor.getZ(),
                    targetPos.x,
                    targetPos.y,
                    targetPos.z
                ).withFeatures(requestedFeatures).start().join();

                if (pathResult.isErr()) {
                    resetBlockBreakExecutor(actor, blackboard);
                    return Result.NO_PATH;
                }
            } else {
                navigator.updateTarget(targetPos.x, targetPos.y, targetPos.z);
            }

            navigator.tick(
                actor.getX(),
                actor.getY(),
                actor.getZ(),
                actor.getBbWidth(),
                actor.getBbHeight()
            );

            handleDoorInteractions(actor, navigator, blackboard);

            var blockBreakResult = blockBreakExecutor(blackboard).tick(actor, navigator);

            switch (blockBreakResult) {
                case BREAKING, IDLE -> { /* NO-OP */ }
                case INVALIDATED -> navigator.requestReplan();
            }

            if (blockBreakResult != PathBlockBreakExecutor.Result.IDLE) {
                return Result.MOVING;
            }

            if (navigatorState.isDone()) {
                return Result.FINISHED;
            }

            var waypointCenter = navigatorState.getCurrentTargetCenter();

            if (waypointCenter == null) {
                resetBlockBreakExecutor(actor, blackboard);
                return Result.NO_PATH;
            }

            PathMovementController.follow(actor, navigator, waypointCenter, speedMultiplier);

            return Result.MOVING;
        } finally {
            PathDebugUtil.sendDebugSearchSnapshot(actor, navigator);
            PathDebugUtil.sendDebugNavState(actor, navigator);
        }
    }

    /**
     * Stops the navigator when the action finishes or is interrupted.
     */
    public static void onFinish(Action.Context<? extends PathfinderMob> context) {
        var blackboard = context.getBlackboard(Blackboard.Scope.ACTION);

        closeDoorIfTracked(context.getActor(), blackboard);
        resetBlockBreakExecutor(context.getActor(), blackboard);

        if (context.getActor() instanceof PathNavigatorUser navigatorUser) {
            var navigator = navigatorUser.getPathNavigator();
            navigator.stop();
            PathDebugUtil.sendDebugSearchSnapshot(context.getActor(), navigator);
            PathDebugUtil.sendDebugNavState(context.getActor(), navigator);
        }
    }

    private static PathBlockBreakExecutor blockBreakExecutor(Blackboard blackboard) {
        var executor = blackboard.getOrDefault(BLOCK_BREAK_EXECUTOR, null);

        if (executor == null) {
            executor = new PathBlockBreakExecutor();
            blackboard.set(BLOCK_BREAK_EXECUTOR, executor);
        }

        return executor;
    }

    private static void resetBlockBreakExecutor(PathfinderMob actor, Blackboard blackboard) {
        var executor = blackboard.getOrDefault(BLOCK_BREAK_EXECUTOR, null);

        if (executor == null) {
            return;
        }

        executor.reset(actor.level());
        blackboard.set(BLOCK_BREAK_EXECUTOR, null);
    }

    private static void handleDoorInteractions(PathfinderMob actor, PathNavigatorApi navigator, Blackboard blackboard) {
        if (!navigator.getState().canOpenDoors()) {
            closeDoorIfTracked(actor, blackboard);
            return;
        }

        var currentTarget = navigator.getState().getCurrentTargetPos();
        if (currentTarget == null) {
            closeDoorIfTracked(actor, blackboard);
            return;
        }

        var level = actor.level();
        var targetState = level.getBlockState(currentTarget);

        if (targetState.getBlock() instanceof DoorBlock doorBlock && !targetState.getValue(DoorBlock.OPEN)) {
            doorBlock.setOpen(actor, level, targetState, currentTarget, true);
            blackboard.set(LAST_OPENED_DOOR_POS, currentTarget);
        }

        var lastOpenedDoorPos = blackboard.getOrDefault(LAST_OPENED_DOOR_POS, null);

        if (
            lastOpenedDoorPos != null
                && !lastOpenedDoorPos.equals(currentTarget)
                && !actor.blockPosition().equals(lastOpenedDoorPos)
        ) {
            closeDoorIfTracked(actor, blackboard);
        }
    }

    private static void closeDoorIfTracked(PathfinderMob actor, Blackboard blackboard) {
        var lastOpenedDoorPos = blackboard.getOrDefault(LAST_OPENED_DOOR_POS, null);

        if (lastOpenedDoorPos == null) {
            return;
        }

        if (
            actor.blockPosition().distSqr(lastOpenedDoorPos) > 9
                || !EntityUtil.canMobSeeBlock(actor, lastOpenedDoorPos.getCenter())
        ) {
            blackboard.set(LAST_OPENED_DOOR_POS, null);
            return;
        }

        var level = actor.level();
        var doorState = level.getBlockState(lastOpenedDoorPos);

        if (doorState.getBlock() instanceof DoorBlock doorBlock && doorState.getValue(DoorBlock.OPEN)) {
            doorBlock.setOpen(actor, level, doorState, lastOpenedDoorPos, false);
        }

        blackboard.set(LAST_OPENED_DOOR_POS, null);
    }

    private NeoMoveToPosAction() {
        throw new UnsupportedOperationException();
    }
}
