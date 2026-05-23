package com.blib.api.common.goap.v1.action.impl;

import com.just.ai.goap.StateKey;
import com.just.ai.goap.action.Action;
import com.just.ai.goap.state.Blackboard;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

public class WanderAction {

    private static final StateKey<Vec3> TARGET_POS = StateKey.sensed("target_pos");

    public static <T extends PathfinderMob> Action.Signal perform(
        Action.Context<? extends T> context,
        int radius,
        int verticalRange,
        double speedMultiplier,
        Consumer<Action.Context<? extends T>> onWanderCompleteCallback
    ) {
        var actor = context.getActor();
        var blackboard = context.getBlackboard(Blackboard.Scope.ACTION);
        var targetPosOrNull = blackboard.getOrNull(TARGET_POS);

        if (targetPosOrNull == null) {
            targetPosOrNull = LandRandomPos.getPos(actor, radius, verticalRange);

            if (targetPosOrNull == null) {
                return Action.Signal.ABORT;
            }

            blackboard.set(TARGET_POS, targetPosOrNull);
        }

        return switch (MoveToPosAction.perform(context, targetPosOrNull, speedMultiplier)) {
            case FINISHED -> {
                onWanderCompleteCallback.accept(context);
                yield Action.Signal.CONTINUE;
            }
            case MOVING -> Action.Signal.CONTINUE;
            case NO_PATH -> Action.Signal.ABORT;
        };
    }

    public static void onFinish(Action.Context<? extends PathfinderMob> context) {
        context.getActor().getNavigation().stop();
    }

    private WanderAction() {
        throw new UnsupportedOperationException();
    }
}
