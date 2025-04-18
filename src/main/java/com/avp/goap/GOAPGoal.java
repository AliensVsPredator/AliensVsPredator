package com.avp.goap;

import com.avp.goap.expression.GOAPConditionSet;

public abstract class GOAPGoal {

    private final GOAPConditionSet desiredWorldState;

    private final String name;

    protected GOAPGoal() {
        this.desiredWorldState = createDesiredConditions();
        this.name = this.getClass().getSimpleName();
    }

    public abstract GOAPConditionSet createDesiredConditions();

    public GOAPConditionSet getDesiredWorldState() {
        return desiredWorldState;
    }

    public String getName() {
        return name;
    }
}
