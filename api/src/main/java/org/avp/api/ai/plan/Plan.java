package org.avp.api.ai.plan;

import org.avp.api.ai.action.Action;
import org.avp.api.ai.goal.Goal;

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

        // Tick remaining goals.
        var remainingGoals = goals.subList(currentGoalIndex, goals.size());
        remainingGoals.forEach(Goal::tick);

        // Execute the current goal.
        var currentGoal = goals.get(currentGoalIndex);
        var bestAction = currentGoal.getBestAction();
        bestAction.ifPresent(Action::execute);

        if (currentGoal.isCompleted()) {
            currentGoal.onComplete();
            currentGoalIndex++;
        }
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
