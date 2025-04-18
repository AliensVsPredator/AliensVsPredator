package com.avp.goap;

import com.avp.goap.expression.GOAPConditionSet;

public abstract class GOAPGoal {

    private final GOAPConditionSet desiredConditions;

    private final String name;

    protected GOAPGoal() {
        this.desiredConditions = createDesiredConditions();
        this.name = this.getClass().getSimpleName();
    }

    protected abstract GOAPConditionSet createDesiredConditions();

    public GOAPConditionSet getDesiredConditions() {
        return desiredConditions;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
