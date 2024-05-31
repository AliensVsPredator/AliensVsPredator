package org.avp.api.entity.ai.plan;

import org.avp.api.entity.ai.goal.Goal;

import java.util.List;

public class Plan {

    private final Goal targetGoal;

    private final List<Goal> preGoals;

    public Plan(Goal targetGoal, List<Goal> preGoals) {
        this.targetGoal = targetGoal;
        this.preGoals = preGoals;
    }

    public void execute() {
        if (!isValid()) {
            return;
        }

        var firstPreGoalOptional = preGoals.stream().findFirst();

        if (firstPreGoalOptional.isPresent()) {
            var firstPreGoal = firstPreGoalOptional.get();
            var isCompleted = executeGoal(firstPreGoal);

            if (isCompleted) {
                preGoals.remove(firstPreGoal);
            }
            return;
        }

        executeGoal(targetGoal);
    }

    private boolean executeGoal(Goal goal) {
        var bestAction = goal.getBestAction();
        bestAction.execute();
        return goal.isCompleted();
    }

    public boolean isValid() {
        return preGoals.stream().allMatch(Goal::isValid) && targetGoal.isValid();
    }

    public boolean isCompleted() {
        return targetGoal.isCompleted();
    }
}
