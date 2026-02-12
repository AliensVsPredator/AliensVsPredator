package com.blib.api.common.goap.v1.action.impl;

import com.just.goap.StateKey;
import com.just.goap.state.Blackboard;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;

public class MoveToPosAction {

    private static final StateKey<Integer> TICKS_UNTIL_NEXT_PATH_RECALCULATION = StateKey.sensed("ticks_until_next_path_recalculation");

    public static Result perform(
        PathfinderMob pathfinderMob,
        Blackboard blackboard,
        Vec3 targetPos,
        double speedMultiplier
    ) {
        var navigation = pathfinderMob.getNavigation();
        var ticksUntilNextPathRecalculation = blackboard.getOrDefault(TICKS_UNTIL_NEXT_PATH_RECALCULATION, 0);

        ticksUntilNextPathRecalculation = Math.max(ticksUntilNextPathRecalculation - 1, 0);

        blackboard.set(TICKS_UNTIL_NEXT_PATH_RECALCULATION, ticksUntilNextPathRecalculation);

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

    private MoveToPosAction() {
        throw new UnsupportedOperationException();
    }

    public enum Result {
        FINISHED,
        MOVING,
        NO_PATH,
    }
}
