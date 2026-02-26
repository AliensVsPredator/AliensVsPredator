package com.blib.api.common.goap.v1.action.impl;

import com.just.goap.StateKey;
import com.just.goap.action.Action;
import com.just.goap.state.Blackboard;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.phys.Vec3;

import com.blib.api.common.entity.v1.EntityUtil;

public class MoveToPosAction {

    private static final StateKey<Integer> TICKS_UNTIL_NEXT_PATH_RECALCULATION = StateKey.sensed("ticks_until_next_path_recalculation");

    private static final StateKey<BlockPos> LAST_OPENED_DOOR_POS = StateKey.sensed("last_opened_door_pos");

    public static Result perform(
        Action.Context<? extends PathfinderMob> context,
        Vec3 targetPos,
        double speedMultiplier
    ) {
        var pathfinderMob = context.getActor();
        var blackboard = context.getBlackboard(Blackboard.Scope.ACTION);
        var navigation = pathfinderMob.getNavigation();
        var ticksUntilNextPathRecalculation = blackboard.getOrDefault(TICKS_UNTIL_NEXT_PATH_RECALCULATION, 0);

        ticksUntilNextPathRecalculation = Math.max(ticksUntilNextPathRecalculation - 1, 0);

        blackboard.set(TICKS_UNTIL_NEXT_PATH_RECALCULATION, ticksUntilNextPathRecalculation);

        handleDoorInteractions(pathfinderMob, blackboard);

        if (ticksUntilNextPathRecalculation <= 0) {
            // Always set the path recompute time after the path creation is attempted.
            ticksUntilNextPathRecalculation = 4 + pathfinderMob.getRandom().nextInt(7);

            var distanceSqr = pathfinderMob.distanceToSqr(targetPos);

            // Penalize tick recalculation for longer distances.
            if (distanceSqr > 1024.0) {
                ticksUntilNextPathRecalculation += 10;
            } else if (distanceSqr > 256.0) {
                ticksUntilNextPathRecalculation += 5;
            }

            Result result;

            if (navigation.moveTo(targetPos.x, targetPos.y, targetPos.z, speedMultiplier)) {
                result = Result.MOVING;
            } else {
                if (pathfinderMob.getNavigation().isDone()) {
                    return Result.FINISHED;
                }

                // Penalize tick recalculation if the computed path was invalid.
                ticksUntilNextPathRecalculation += 15;
                result = Result.NO_PATH;
            }

            // Always set the ticks until next path recalculation.
            blackboard.set(TICKS_UNTIL_NEXT_PATH_RECALCULATION, ticksUntilNextPathRecalculation);

            return result;
        }

        return Result.MOVING;
    }

    private static void handleDoorInteractions(PathfinderMob pathfinderMob, Blackboard blackboard) {
        if (
            !(pathfinderMob.getNavigation() instanceof GroundPathNavigation groundNavigation)
                || !groundNavigation.canOpenDoors()
        ) {
            return;
        }

        var path = groundNavigation.getPath();

        if (path == null || path.isDone()) {
            closeDoorIfTracked(pathfinderMob, blackboard);
            return;
        }

        var level = pathfinderMob.level();
        var nextNodeIndex = path.getNextNodeIndex();

        // Open a closed door at the next node.
        if (nextNodeIndex < path.getNodeCount()) {
            var nextNode = path.getNode(nextNodeIndex);
            var nextPos = nextNode.asBlockPos();
            var nextState = level.getBlockState(nextPos);

            if (nextState.getBlock() instanceof DoorBlock doorBlock && !nextState.getValue(DoorBlock.OPEN)) {
                doorBlock.setOpen(pathfinderMob, level, nextState, nextPos, true);
                blackboard.set(LAST_OPENED_DOOR_POS, nextPos);
            }
        }

        // Close the door at the previous node if it was opened by this entity.
        if (nextNodeIndex > 0) {
            var prevNode = path.getNode(nextNodeIndex - 1);
            var prevPos = prevNode.asBlockPos();
            var lastOpenedDoorPos = blackboard.getOrDefault(LAST_OPENED_DOOR_POS, null);

            if (
                lastOpenedDoorPos != null && lastOpenedDoorPos.equals(prevPos)
                    && !pathfinderMob.blockPosition().equals(prevPos)
            ) {
                var prevState = level.getBlockState(prevPos);

                if (prevState.getBlock() instanceof DoorBlock doorBlock && prevState.getValue(DoorBlock.OPEN)) {
                    doorBlock.setOpen(pathfinderMob, level, prevState, prevPos, false);
                }

                blackboard.set(LAST_OPENED_DOOR_POS, null);
            }
        }
    }

    private static void closeDoorIfTracked(PathfinderMob pathfinderMob, Blackboard blackboard) {
        var lastOpenedDoorPos = blackboard.getOrDefault(LAST_OPENED_DOOR_POS, null);

        if (lastOpenedDoorPos == null) {
            return;
        }

        if (
            // If the door is further than 3 blocks away...
            pathfinderMob.blockPosition().distSqr(lastOpenedDoorPos) > 9
                // OR if we can't see the door...
                || !EntityUtil.canMobSeeBlock(pathfinderMob, lastOpenedDoorPos.getCenter())
        ) {
            // Then return early, we can't close the door.
            blackboard.set(LAST_OPENED_DOOR_POS, null);
            return;
        }

        var level = pathfinderMob.level();
        var doorState = level.getBlockState(lastOpenedDoorPos);

        if (doorState.getBlock() instanceof DoorBlock doorBlock && doorState.getValue(DoorBlock.OPEN)) {
            doorBlock.setOpen(pathfinderMob, level, doorState, lastOpenedDoorPos, false);
        }

        blackboard.set(LAST_OPENED_DOOR_POS, null);
    }

    private MoveToPosAction() {
        throw new UnsupportedOperationException();
    }

    public enum Result {
        FINISHED,
        MOVING,
        NO_PATH,
    }
}
