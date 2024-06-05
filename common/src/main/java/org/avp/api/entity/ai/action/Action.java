package org.avp.api.entity.ai.action;

import org.avp.api.entity.ai.CostConstraint;

public abstract class Action {

    private final CostConstraint costConstraint = createCostConstraint();

    protected abstract CostConstraint createCostConstraint();

    /**
     * The cost to execute this action. Lower is better.
     */
    public abstract int getCost();
    public abstract boolean isValid();
    public abstract void execute();

    public void tick() {}

    public void onComplete() {}

    public CostConstraint getCostConstraint() {
        return costConstraint;
    }
}
