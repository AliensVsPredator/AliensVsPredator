package com.blib.mod.client.render.goap;

import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blib.mod.client.render.goap.model.DisplayPlan;
import com.blib.mod.client.render.goap.model.GOAPAgentDebugData;
import com.blib.mod.client.render.goap.model.GOAPPlanDebugData;

@ApiStatus.Internal
public final class GOAPDebugAgentDisplayState {

    private final Map<String, DisplayPlan> activePlansByGoal;

    private final List<DisplayPlan> ghostPlans;

    public GOAPDebugAgentDisplayState() {
        this.activePlansByGoal = new HashMap<>();
        this.ghostPlans = new ArrayList<>();
    }

    public void update(GOAPAgentDebugData agent) {
        var now = System.currentTimeMillis();
        var incomingGoals = new HashMap<String, GOAPPlanDebugData>();

        for (var plan : agent.plans()) {
            incomingGoals.put(plan.goalName(), plan);
        }

        // Remove ghost plans whose goal was replanned, or whose timer expired.
        ghostPlans.removeIf(ghost -> incomingGoals.containsKey(ghost.plan().goalName()) || ghost.isExpired(now));

        // Detect plans that disappeared or reached a terminal state -> become ghosts.
        var previousGoals = new HashMap<>(activePlansByGoal);
        activePlansByGoal.clear();

        for (var entry : previousGoals.entrySet()) {
            var goalName = entry.getKey();
            var prev = entry.getValue();

            if (!incomingGoals.containsKey(goalName)) {
                // Plan disappeared entirely. Treat as finished (server removed it).
                ghostPlans.add(new DisplayPlan(prev.plan(), "FINISHED", now));
            }
        }

        // Process incoming plans.
        for (var goapPlanDebugData : agent.plans()) {
            if (isTerminalState(goapPlanDebugData.planState())) {
                // Plan reached a terminal state but is still reported. Ghost it.
                ghostPlans.add(new DisplayPlan(goapPlanDebugData, goapPlanDebugData.planState(), now));
            } else {
                activePlansByGoal.put(goapPlanDebugData.goalName(), new DisplayPlan(goapPlanDebugData, null, 0));
            }

            // If this goal had a ghost (e.g. failed then replanned), remove the ghost.
            ghostPlans.removeIf(ghost -> ghost.plan().goalName().equals(goapPlanDebugData.goalName()));
        }
    }

    public List<DisplayPlan> getDisplayPlans() {
        var now = System.currentTimeMillis();
        // Active plans first.
        var result = new ArrayList<>(activePlansByGoal.values());

        // Then non-expired ghosts.
        for (var ghost : ghostPlans) {
            if (!ghost.isExpired(now)) {
                result.add(ghost);
            }
        }

        return result;
    }

    static boolean isTerminalState(String planState) {
        return "FINISHED".equals(planState) || "ABORTED".equals(planState) || "INVALID".equals(planState);
    }

}
