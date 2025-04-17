package com.avp.goap;

import com.bvanseg.just.functional.option.Option;

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

    public State update(T context) {
        if (currentActionIndex >= actions.size()) {
            return new State.Finished();
        }

        var action = actions.get(currentActionIndex);
        var done = action.perform(context, blackboard);

        if (done) {
            currentActionIndex++;

            if (currentActionIndex >= actions.size()) {
                return new State.Finished();
            }
        }

        return new State.InProgress();
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
