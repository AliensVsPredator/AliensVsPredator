package org.avp.api.entity.ai.plan;

import org.avp.api.entity.ai.goal.Goal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class Planner {

    private Plan plan;

    private final Set<Goal> availableGoals;

    public Planner(Set<Goal> availableGoals) {
        this.availableGoals = availableGoals;
    }

    public void tick() {
        if (plan == null || !plan.isValid() || plan.isCompleted()) {
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
        var preGoalsCollector = new ArrayList<Goal>();
        var succeeded = accumulateValidPreGoalsForTargetGoal(goal, preGoalsCollector);

        if (!succeeded) {
            return Optional.empty();
        }

        return Optional.of(new Plan(goal, preGoalsCollector));
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
            var goalForPrerequisite = availableGoals.stream()
                .filter(goal -> Objects.equals(prerequisite, goal.getProgresses().orElse(null)))
                .findFirst();

            var isValidPrerequisiteGoal = goalForPrerequisite.filter(goal -> accumulateValidPreGoalsForTargetGoal(goal, accumulatedGoals))
                .isPresent();

            if (!isValidPrerequisiteGoal) {
                return false;
            }
        }

        if (targetGoal.isValid()) {
            accumulatedGoals.add(targetGoal);
            return true;
        }

        return false;
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
