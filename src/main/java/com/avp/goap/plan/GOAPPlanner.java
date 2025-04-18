package com.avp.goap.plan;

import com.bvanseg.just.functional.option.Option;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

import com.avp.goap.GOAP;
import com.avp.goap.GOAPAction;
import com.avp.goap.GOAPGoal;
import com.avp.goap.state.GOAPWorldState;

public class GOAPPlanner<T> {

    private final GOAP<T> goap;

    public GOAPPlanner(GOAP<T> goap) {
        this.goap = goap;
    }

    public Option<GOAPPlan<T>> createPlan(T context, GOAPWorldState currentState, Collection<GOAPGoal> goals) {
        GOAPPlan<T> bestPlan = null;
        float bestCost = Float.MAX_VALUE;

        for (var goal : goals) {
            if (currentState.satisfies(goal.getDesiredWorldState())) {
                // No plan needed — already satisfied.
                continue;
            }

            var plan = buildPlan(context, currentState, goal);

            if (plan == null) {
                continue;
            }

            var cost = plan.stream()
                .map(action -> action.getCost(context, currentState))
                .reduce(0.0f, Float::sum);

            if (cost < bestCost) {
                bestCost = cost;
                bestPlan = new GOAPPlan<>(goal, plan);
            }
        }

        return Option.ofNullable(bestPlan);
    }

    private List<GOAPAction<T>> buildPlan(T context, GOAPWorldState currentState, GOAPGoal goal) {
        var openSet = new PriorityQueue<Node<T>>(Comparator.comparingDouble(n -> n.cost));
        var closedSet = new HashSet<GOAPWorldState>();

        openSet.add(new Node<>(currentState, new ArrayList<>(), 0.0f));

        while (!openSet.isEmpty()) {
            var node = openSet.poll();

            if (!node.plan.isEmpty() && node.state.satisfies(goal.getDesiredWorldState())) {
                return node.plan;
            }

            closedSet.add(node.state);

            for (var action : goap.getAvailableActions()) {
                if (!action.getPreconditions().satisfiedBy(node.state)) {
                    continue;
                }

                var newState = node.state.applyEffects(action.getEffects());

                if (closedSet.contains(newState)) {
                    continue;
                }

                var newPlan = new ArrayList<>(node.plan);

                newPlan.add(action);
                openSet.add(new Node<>(newState, newPlan, node.cost + action.getCost(context, currentState)));
            }
        }

        return null;
    }

    private record Node<T>(
        GOAPWorldState state,
        List<GOAPAction<T>> plan,
        float cost
    ) {}
}
