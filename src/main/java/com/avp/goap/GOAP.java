package com.avp.goap;

import com.bvanseg.just.functional.option.Option;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GOAP<T> {

    private final Set<GOAPGoal> goals;

    private final GOAPPlanner<T> planner;

    private final List<GOAPSensor<T>> sensors;

    private @NotNull Option<GOAPPlan<T>> currentPlanOption;

    public GOAP() {
        this.goals = new HashSet<>();
        this.planner = new GOAPPlanner<>();
        this.sensors = new ArrayList<>();
        this.currentPlanOption = Option.none();
    }

    public void addGoal(GOAPGoal goal) {
        goals.add(goal);
    }

    public void addSensor(GOAPSensor<T> sensor) {
        sensors.add(sensor);
    }

    public GOAPWorldState sense(T context) {
        var state = new GOAPWorldState();

        for (var sensor : sensors) {
            sensor.sense(context, state);
        }

        return state;
    }

    public void update(T context) {
        // Sense all world input that we need to.
        var worldState = sense(context);

        if (currentPlanOption.isNone()) {
            this.currentPlanOption = planner.createPlan(worldState, goals);
        }

        currentPlanOption.ifSome(plan -> {
            var planState = plan.update(context);

            switch (planState) {
                case GOAPPlan.State.Failed failed -> this.currentPlanOption = Option.none();
                case GOAPPlan.State.Finished finished -> this.currentPlanOption = Option.none();
                case GOAPPlan.State.Invalid invalid -> this.currentPlanOption = Option.none();
                case GOAPPlan.State.InProgress inProgress -> {}
            }
        });
    }
}
