package com.blib.api.common.goap.v1.plan;

import com.just.ai.goap.plan.Plan;
import com.just.ai.goap.plan.executor.impl.ConcurrentPlanExecutor;
import com.just.ai.goap.state.ReadableWorldState;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.blib.api.common.goap.v1.action.ActionMask;
import com.blib.api.common.goap.v1.action.BLibAction;

public class ActionMaskPlanResolver<T> implements ConcurrentPlanExecutor.PlanResolver<T> {

    @Override
    public Resolution resolve(Plan<T> plan, Plan<T> plan1, T t, ReadableWorldState readableWorldState) {
        var actionMasks = getActionMasksForPlan(plan);
        var otherActionMasks = getActionMasksForPlan(plan1);

        var noMasksInCommon = Collections.disjoint(actionMasks, otherActionMasks);

        if (noMasksInCommon) {
            // No masks in common for the plans, so fall back on same-goal cost analysis for resolution.
            return ConcurrentPlanExecutor.PlanResolver.<T>preferCheaperSameGoal()
                .resolve(plan, plan1, t, readableWorldState);
        }

        // Incoming plan shares masks with active plan, so do not accept incoming plan.
        return Resolution.KEEP_ACTIVE;
    }

    private Set<ActionMask> getActionMasksForPlan(Plan<T> plan) {
        return plan.getActions()
            .stream()
            .flatMap(action -> {
                if (action instanceof BLibAction<?> bLibAction) {
                    return bLibAction.getMasks().stream();
                }

                return Stream.empty();
            })
            .collect(Collectors.toSet());
    }
}
