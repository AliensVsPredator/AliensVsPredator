package com.blib.internal.common.model.state_machine;

public interface State<C extends StateMachineContext> {

    void onEnter(C context);

    void onUpdate(C context);

    void onExit(C context);
}
