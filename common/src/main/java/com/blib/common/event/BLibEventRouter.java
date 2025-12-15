package com.blib.common.event;

public interface BLibEventRouter<Dispatcher> {

    Dispatcher dispatcher();

    void register(Dispatcher dispatcher);
}
