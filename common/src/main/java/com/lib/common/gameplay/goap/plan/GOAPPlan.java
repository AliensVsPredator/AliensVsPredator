package com.lib.common.gameplay.goap.plan;

import com.just.core.functional.option.Option;
import com.lib.common.gameplay.goap.GOAPAction;
import com.lib.common.gameplay.goap.GOAPGoal;
import com.lib.common.gameplay.goap.state.GOAPBlackboard;
import com.lib.common.gameplay.goap.state.GOAPWorldState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GOAPPlan<T> {

    private final GOAPGoal goal;

    private final List<GOAPAction<T>> actions;

    private final GOAPBlackboard blackboard;

    private int currentActionIndex;

    public GOAPPlan(GOAPGoal goal, List<GOAPAction<T>> actions) {
        this.goal = goal;
        this.actions = actions;
        this.blackboard = new GOAPBlackboard();
        this.currentActionIndex = 0;
    }

    public State update(T context, GOAPWorldState currentState) {
        if (currentActionIndex >= actions.size()) {
            return new State.Finished();
        }

        var action = actions.get(currentActionIndex);

        if (!action.getPreconditions().satisfiedBy(currentState)) {
            action.onFinish(context, currentState, blackboard);
            return new State.Invalid();
        }

        // If the world state already satisfies the effects of the action,
        // OR check if the action is finished after performing...
        if (currentState.satisfies(action.getEffects()) || action.perform(context, currentState, blackboard)) {
            // Then the action can be considered complete, move on to the next action or finish.
            action.onFinish(context, currentState, blackboard);
            return proceedToNextActionOrFinish();
        }

        return new State.InProgress();
    }

    private @NotNull State proceedToNextActionOrFinish() {
        // Move to the next action index.
        currentActionIndex += 1;
        // Evaluate based on current index if we've reached the end of the plan sequence or have more actions left.
        return currentActionIndex >= actions.size() ? new State.Finished() : new State.InProgress();
    }

    public Option<GOAPAction<T>> getCurrentAction() {
        return currentActionIndex >= actions.size()
            ? Option.none()
            : Option.some(actions.get(currentActionIndex));
    }

    public sealed interface State {

        record Finished() implements State {}

        record Failed() implements State {}

        record InProgress() implements State {}

        record Invalid() implements State {}
    }
}
