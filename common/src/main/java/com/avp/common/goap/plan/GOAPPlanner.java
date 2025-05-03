package com.avp.common.goap.plan;

import com.bvanseg.just.functional.option.Option;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.avp.common.goap.GOAP;
import com.avp.common.goap.GOAPAction;
import com.avp.common.goap.GOAPGoal;
import com.avp.common.goap.condition.GOAPConditionContainer;
import com.avp.common.goap.state.GOAPMutableWorldState;
import com.avp.common.goap.state.GOAPWorldState;

public class GOAPPlanner<T> {

    private final GOAP<T> goap;

    public GOAPPlanner(GOAP<T> goap) {
        this.goap = goap;
    }

    public Option<GOAPPlan<T>> createPlan(T context, GOAPWorldState currentState, Collection<GOAPGoal> goals) {
        GOAPPlan<T> bestPlan = null;
        float bestCost = Float.MAX_VALUE;

        for (var goal : goals) {
            // We need to find actions that satisfy these conditions.
            var desiredConditions = goal.getDesiredConditions();

            var planOption = buildPlanForConditions(desiredConditions, currentState);

            if (planOption.isSomeAnd(plan -> !plan.isEmpty())) {
                var plan = planOption.unwrap();

                var cost = plan.stream()
                    .map(action -> action.getCost(context, currentState))
                    .reduce(0.0f, Float::sum);

                if (cost < bestCost) {
                    bestCost = cost;
                    bestPlan = new GOAPPlan<>(goal, plan);
                }
            }
        }

        return Option.ofNullable(bestPlan);
    }

    private Option<List<GOAPAction<T>>> buildPlanForConditions(GOAPConditionContainer desiredConditions, GOAPWorldState currentState) {
        if (currentState.satisfies(desiredConditions)) {
            return Option.some(List.of());
        }

        var plan = new ArrayList<GOAPAction<T>>();
        var workingState = new GOAPMutableWorldState(currentState);

        for (var condition : desiredConditions.getConditions()) {
            if (condition.satisfiedBy(workingState)) {
                continue;
            }

            var satisfyingActions = goap.getAvailableActions()
                .stream()
                .filter(action -> condition.satisfiedBy(action.getEffects()))
                .toList();

            var satisfied = false;

            for (var action : satisfyingActions) {
                var subPlanOption = buildPlanForConditions(action.getPreconditions(), workingState);

                if (subPlanOption.isSome()) {
                    var subPlan = subPlanOption.unwrap();
                    plan.addAll(subPlan);
                    plan.add(action);
                    workingState.apply(action.getEffects());
                    satisfied = true;
                    break;
                }
            }

            if (!satisfied) {
                return Option.none();
            }
        }

        return Option.some(plan);
    }
}
