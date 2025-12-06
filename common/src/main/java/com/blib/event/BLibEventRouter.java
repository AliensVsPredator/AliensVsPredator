package com.blib.event;

public interface BLibEventRouter<Dispatcher> {

    Dispatcher dispatcher();

     void register(Dispatcher dispatcher);
}
