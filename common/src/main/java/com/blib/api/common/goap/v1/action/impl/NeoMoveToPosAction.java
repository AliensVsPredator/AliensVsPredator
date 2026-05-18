package com.blib.api.common.goap.v1.action.impl;

import com.just.ai.goap.action.Action;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;

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

    /**
     * Result of a movement tick.
     */
    public enum Result {
        FINISHED,
        MOVING,
        NO_PATH,
        WAITING_FOR_BLOCK_BREAK
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

        if (!(actor instanceof PathNavigatorUser navigatorUser)) {
            return Result.NO_PATH;
        }

        var navigator = navigatorUser.getPathNavigator();
        var targetBlockPos = BlockPos.containing(targetPos);

        if (actor instanceof Mob mob) {
            navigator.setDebugCaptureEnabled(PathDebugUtil.hasDebugWatchers(mob));
        } else {
            navigator.setDebugCaptureEnabled(false);
        }

        if (!navigator.isNavigating()) {
            var found = navigator.navigateTo(actor.getX(), actor.getY(), actor.getZ(), targetBlockPos);

            if (!found) {
                return Result.NO_PATH;
            }
        } else {
            navigator.updateTarget(targetBlockPos);
        }

        navigator.tick(
            actor.getX(),
            actor.getY(),
            actor.getZ(),
            actor.getBbWidth(),
            actor.getBbHeight()
        );

        if (actor instanceof Mob mob) {
            PathDebugUtil.sendDebugSearchSnapshot(mob, navigator);
            PathDebugUtil.sendDebugNavState(mob, navigator);
        }

        if (navigator.isDone()) {
            return Result.FINISHED;
        }

        if (navigator.isWaitingForBlockBreak()) {
            return Result.WAITING_FOR_BLOCK_BREAK;
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
        if (context.getActor() instanceof PathNavigatorUser navigatorUser) {
            navigatorUser.getPathNavigator().stop();
        }
    }

    private NeoMoveToPosAction() {
        throw new UnsupportedOperationException();
    }
}
