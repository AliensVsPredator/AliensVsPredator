package com.avp.goap;

import com.avp.goap.state.GOAPWorldState;

public abstract class GOAPGoal {

    private final GOAPWorldState desiredWorldState;

    private final String name;

    protected GOAPGoal() {
        this.desiredWorldState = createDesiredWorldState();
        this.name = this.getClass().getSimpleName();
    }

    public abstract GOAPWorldState createDesiredWorldState();

    public GOAPWorldState getDesiredWorldState() {
        return desiredWorldState;
    }

    public String getName() {
        return name;
    }
}
