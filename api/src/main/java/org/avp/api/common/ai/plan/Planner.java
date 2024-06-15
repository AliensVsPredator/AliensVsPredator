package org.avp.api.common.ai.plan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.avp.api.common.ai.goal.Goal;

public class Planner {

    private Plan plan;

    private final Set<Goal> availableGoals;

    public Planner() {
        this.availableGoals = new HashSet<>();
    }

    public void addGoal(Goal goal) {
        availableGoals.add(goal);
    }

    public void tick() {
        if (plan == null || !plan.isValid() || plan.isCompleted()) {
            this.plan = null;
            var bestPlanOptional = getBestPlan();
            bestPlanOptional.ifPresent(bestPlan -> this.plan = bestPlan);
        }

        if (plan != null) {
            plan.execute();
        }
    }

    public Optional<Plan> getBestPlan() {
        var bestGoalOptional = findBestGoal();
        return bestGoalOptional.flatMap(this::createPlan);
    }

    /**
     * Attempts to create a plan for a given goal.
     */
    public Optional<Plan> createPlan(Goal goal) {
        var goalsCollector = new ArrayList<Goal>();
        var succeeded = accumulateValidPreGoalsForTargetGoal(goal, goalsCollector);

        if (!succeeded) {
            return Optional.empty();
        }

        return Optional.of(new Plan(goalsCollector));
    }

    /**
     * Accumulates a list of **valid** prerequisite goals for a given goal.
     *
     * @return true if all goals were valid, false otherwise.
     */
    private boolean accumulateValidPreGoalsForTargetGoal(Goal targetGoal, List<Goal> accumulatedGoals) {
        var prerequisiteOptional = targetGoal.getProgressedBy();

        if (prerequisiteOptional.isPresent()) {
            var prerequisite = prerequisiteOptional.get();
            var goalForPrerequisiteOptional = availableGoals.stream()
                .filter(goal -> Objects.equals(prerequisite, goal.getProgresses().orElse(null)))
                .findFirst();

            // If no goal could be found for the prerequisite, it is impossible to complete the plan.
            if (goalForPrerequisiteOptional.isEmpty()) {
                return false;
            }

            var goalForPrerequisite = goalForPrerequisiteOptional.get();

            var isChainValid = accumulateValidPreGoalsForTargetGoal(goalForPrerequisite, accumulatedGoals);

            // If the goal is not valid, the plan is not sound, so we must assume it is impossible.
            if (!isChainValid) {
                return false;
            }
        }

        if (targetGoal.isValid() && !targetGoal.isCompleted()) {
            accumulatedGoals.add(targetGoal);
            return true;
        }

        return targetGoal.isCompleted();
    }

    /**
     * Finds the first goal that is valid.
     */
    private Optional<Goal> findBestGoal() {
        return availableGoals.stream()
            .filter(goal -> goal.isValid() && goal.getProgresses().isEmpty())
            .findFirst();
    }
}
