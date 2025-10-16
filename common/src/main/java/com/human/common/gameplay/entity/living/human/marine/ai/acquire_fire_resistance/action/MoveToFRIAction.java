package com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.action;

import com.human.common.gameplay.entity.living.human.marine.ai.acquire_fire_resistance.FRISensors;
import com.just.core.functional.option.Option;
import com.just.goap.Action;
import com.just.goap.StateKey;
import com.just.goap.state.Blackboard;
import com.just.goap.state.ReadableWorldState;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.pathfinder.Path;

public class MoveToFRIAction {

    private static final StateKey<Path> PATH_TO_FRI = StateKey.sensed("path_to_fri");

    public static Action.Signal perform(PathfinderMob pathfinderMob, ReadableWorldState worldState, Blackboard blackboard) {
        var worldItemTargetOption = worldState.getOrDefault(FRISensors.BEST_FRI_IN_WORLD.key(), Option.none());

        if (worldItemTargetOption.isNone()) {
            return Action.Signal.ABORT;
        }

        var itemEntity = worldItemTargetOption.unwrap().itemTarget().itemEntity();

        var path = blackboard.getOrNull(PATH_TO_FRI);

        if (path == null) {
            path = pathfinderMob.getNavigation().createPath(itemEntity, 0);
        }

        if (path != null) {
            pathfinderMob.getNavigation().moveTo(path, 1);
        }

        return Action.Signal.CONTINUE;
    }

    public static void onFinish(PathfinderMob pathfinderMob, ReadableWorldState worldState, Blackboard blackboard) {
        pathfinderMob.getNavigation().stop();
    }

    private MoveToFRIAction() {
        throw new UnsupportedOperationException();
    }
}
