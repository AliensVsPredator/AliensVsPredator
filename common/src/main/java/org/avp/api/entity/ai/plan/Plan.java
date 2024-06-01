package org.avp.api.entity.ai.plan;

import org.avp.api.entity.ai.goal.Goal;

import java.util.List;

public class Plan {

    private int currentGoalIndex;

    private final List<Goal> goals;

    public Plan(List<Goal> goals) {
        this.currentGoalIndex = 0;
        this.goals = goals;
    }

    public void execute() {
        if (!isValid()) {
            return;
        }

        var currentGoal = goals.get(currentGoalIndex);
        var isCompleted = executeGoal(currentGoal);

        if (isCompleted) {
            currentGoalIndex++;
        }
    }

    private boolean executeGoal(Goal goal) {
        var bestAction = goal.getBestAction();
        bestAction.execute();
        return goal.isCompleted();
    }

    public boolean isValid() {
        if (goals.isEmpty() || currentGoalIndex >= goals.size()) {
            return false;
        }

        var previousGoals = goals.subList(0, currentGoalIndex);
        var remainingGoals = goals.subList(currentGoalIndex, goals.size());
        return areGoalsCompleted(previousGoals) && areGoalsValid(remainingGoals);
    }

    public boolean isCompleted() {
        if (goals.isEmpty() || currentGoalIndex >= goals.size()) {
            return true;
        }

        return currentGoalIndex == goals.size() - 1;
    }

    private boolean areGoalsCompleted(List<Goal> goals) {
        return goals.stream().allMatch(Goal::isCompleted);
    }

    private boolean areGoalsValid(List<Goal> goals) {
        return goals.stream().allMatch(Goal::isValid);
    }
}
