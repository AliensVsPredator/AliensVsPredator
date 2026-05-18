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
import com.blib.api.common.pathfinding.v1.debug.PathDebugUtil;
import com.blib.api.common.pathfinding.v1.navigator.PathNavigator;
import com.blib.api.common.pathfinding.v1.navigator.PathNavigatorUser;

/**
 * GOAP action utility for pathfinding using BLib's {@link PathNavigator}. Requires the entity to implement
 * {@link PathNavigatorUser}.
 * <p>
 * Unlike {@link MoveToPosAction} which delegates to Minecraft's vanilla PathNavigation, this action uses BLib's
 * standalone pathfinding system with multi-terrain support.
 * </p>
 */
public final class NeoMoveToPosAction {

    private static final StateKey<BlockPos> LAST_OPENED_DOOR_POS = StateKey.sensed("neo_last_opened_door_pos");

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
        var actor = context.getActor();
        var blackboard = context.getBlackboard(Blackboard.Scope.ACTION);

        if (!(actor instanceof PathNavigatorUser navigatorUser)) {
            return Result.NO_PATH;
        }

        var navigator = navigatorUser.getPathNavigator();

        if (actor instanceof Mob mob) {
            navigator.setDebugCaptureEnabled(PathDebugUtil.hasDebugWatchers(mob));
        } else {
            navigator.setDebugCaptureEnabled(false);
        }

        if (!navigator.isNavigating()) {
            var found = navigator.navigateTo(
                actor.getX(),
                actor.getY(),
                actor.getZ(),
                targetPos.x,
                targetPos.y,
                targetPos.z
            );

            if (!found) {
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

        if (actor instanceof Mob mob) {
            PathDebugUtil.sendDebugSearchSnapshot(mob, navigator);
            PathDebugUtil.sendDebugNavState(mob, navigator);
        }

        if (navigator.isDone()) {
            return Result.FINISHED;
        }

        var waypointCenter = navigator.getCurrentTargetCenter();

        if (waypointCenter == null) {
            return Result.NO_PATH;
        }

        actor.getMoveControl()
            .setWantedPosition(
                waypointCenter.x,
                waypointCenter.y,
                waypointCenter.z,
                speedMultiplier
            );

        return Result.MOVING;
    }

    /**
     * Stops the navigator when the action finishes or is interrupted.
     */
    public static void onFinish(Action.Context<? extends PathfinderMob> context) {
        closeDoorIfTracked(context.getActor(), context.getBlackboard(Blackboard.Scope.ACTION));

        if (context.getActor() instanceof PathNavigatorUser navigatorUser) {
            navigatorUser.getPathNavigator().stop();
        }
    }

    private static void handleDoorInteractions(PathfinderMob actor, PathNavigator navigator, Blackboard blackboard) {
        if (!navigator.canOpenDoors()) {
            closeDoorIfTracked(actor, blackboard);
            return;
        }

        var currentTarget = navigator.getCurrentTargetPos();
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
