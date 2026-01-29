package com.blib.azurelib.common.util.state;

public abstract class StateMachine<C extends StateMachineContext, T extends State<C>> {

    private final C reusableContext;

    private T state;

    public StateMachine(T initialState) {
        this.state = initialState;
        this.reusableContext = createContext();
    }

    protected abstract C createContext();

    public void update(C context) {
        state.onUpdate(context);
    }

    public C getContext() {
        return reusableContext;
    }

    public T getState() {
        return state;
    }

    public void setState(T newState) {
        state.onExit(reusableContext);
        this.state = newState;
        newState.onEnter(reusableContext);
    }
}
